package io.pivotal.refarch.cqrs.trader.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.CreateOrderBookCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

@RestController
@RequestMapping(value = "/command")
public class CommandController implements BeanClassLoaderAware {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final List<Class> BASE_COMMAND_CLASS = Arrays.asList(
            CreateOrderBookCommand.class, TransactionCommand.class, PortfolioCommand.class,
            CompanyCommand.class, // Should be removed once Company aggregate is reverted to an Entity
            UserCommand.class // Should be removed once User aggregate is reverted to an Entity
    );
    private static final String CORE_API_PACKAGE = "io/pivotal/refarch/cqrs/trader/coreapi";

    private final CommandGateway commandGateway;
    private final ObjectMapper objectMapper;
    private final JsonSchemaGenerator jsonSchemaGenerator;

    private Map<String, String> simpleToFullClassName;
    private Map<String, String> commandApi;
    private ClassLoader classLoader;

    public CommandController(CommandGateway commandGateway,
                             ObjectMapper objectMapper,
                             JsonSchemaGenerator jsonSchemaGenerator) {
        this.commandGateway = commandGateway;
        this.objectMapper = objectMapper;
        this.jsonSchemaGenerator = jsonSchemaGenerator;
    }

    @GetMapping("/api")
    public Map<String, String> getCommandApi() {
        if (commandApi == null) {
            initializeCommandApi();
        }

        logger.debug("Getting the list of API commands. Returning all {} known commands.", commandApi.size());
        return commandApi;
    }

    @PostConstruct
    private void initializeCommandApi() {
        logger.info("Initialising the command API list.");

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        BASE_COMMAND_CLASS.forEach(superCmdClazz -> provider.addIncludeFilter(new AssignableTypeFilter(superCmdClazz)));

        List<String> commandClassNames = provider.findCandidateComponents(CORE_API_PACKAGE)
                                                 .stream()
                                                 .map(BeanDefinition::getBeanClassName)
                                                 .collect(Collectors.toList());

        simpleToFullClassName = commandClassNames.stream().collect(Collectors.toMap(
                this::simpleCommandClassName, commandClassName -> commandClassName
        ));

        commandApi = commandClassNames.stream().collect(Collectors.toMap(
                this::simpleCommandClassName, this::commandJsonSchema
        ));

        logger.info("{} commands available.", commandApi.size());
    }

    private String commandJsonSchema(String commandClassName) {
        try {
            JsonSchema commandSchema = jsonSchemaGenerator.generateSchema(classLoader.loadClass(commandClassName));
            commandSchema.setId(null);
            return objectMapper.writeValueAsString(commandSchema);
        } catch (ClassNotFoundException | JsonProcessingException e) {
            logger.error("Failed to instantiate command api for [{}]", commandClassName, e);
            return String.format("{\n\tmessage: Failed to instantiate command api for [%s]\n}", commandClassName);
        }
    }

    private String simpleCommandClassName(String commandClassName) {
        return commandClassName.substring(commandClassName.lastIndexOf(".") + 1);
    }

    /**
     * A POST endpoint which allows you to publish a command in the Trader App.
     * The {@code commandType} is specified as a path variable of type {@link String}. If the {@code commandType} does
     * not exist, a {@link org.springframework.http.HttpStatus#NOT_FOUND} will be returned.
     * The {@code commandPayloadJson} should be a JSON representation of the command to perform, which might be
     * {@code null} in case the command does not require any input parameters. If the {@code commandPayloadJson} cannot
     * be serialized, a {@link org.springframework.http.HttpStatus#NOT_FOUND} will be returned.
     * If the type exist and the payload can be serialized to an actual command object, the command will be published
     * on to the {@link CommandGateway} to be handled by their dedicated Command Handling functions.
     * The result of this operation will be a {@link CompletableFuture} of generic type {@link ResponseEntity}.
     * The ResponseEntity will contain the eventual result of command handling, which typically only contain an actual
     * result for aggregate creation commands. In that scenario, the aggregate identifier will be returned as a {@link
     * String}.
     *
     * @param commandType        a {@link String} specifying the command type to perform
     * @param commandPayloadJson a {@link String} JSON representation of the command to perform. Might be {@code null}
     *                           if the command does not require a payload
     * @return a {@link CompletableFuture} of generic type {@link ResponseEntity}, containing the optional command
     * handling result as its body
     */
    @PostMapping("/{command-type}")
    public CompletableFuture<ResponseEntity> postCommand(@PathVariable("command-type") String commandType,
                                                         @RequestBody(required = false) String commandPayloadJson) {
        if (commandApi == null) {
            initializeCommandApi();
        }

        logger.debug("Attempting to execute the '{}' command...", commandType);

        String fullCommandClassName = simpleToFullClassName.get(commandType);
        if (fullCommandClassName == null) {
            logger.warn("Tried to publish the command[{}] which does not exist.", commandType);
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }


        Object command;
        try {
            Class<?> commandClass = classLoader.loadClass(fullCommandClassName);
            // If the payload is null, we assume the command does not require any parameters to create.
            command = commandPayloadJson == null
                    ? commandClass.newInstance()
                    : objectMapper.readValue(commandPayloadJson, commandClass);
        } catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
            logger.error("Failed to instantiate command for [{}] and json [{}]", commandType, commandPayloadJson, e);
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }

        return commandGateway.send(command).thenApply(ResponseEntity::ok);
    }

    @Override
    public void setBeanClassLoader(@NotNull ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
