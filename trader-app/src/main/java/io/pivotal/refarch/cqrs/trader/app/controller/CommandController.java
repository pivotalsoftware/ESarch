package io.pivotal.refarch.cqrs.trader.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import io.pivotal.refarch.cqrs.trader.app.query.users.UserCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.CreateOrderBookCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
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
        return commandApi;
    }

    private void initializeCommandApi() {
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

    @PostMapping("/{command-type}")
    public CompletableFuture<ResponseEntity> postCommand(@PathVariable("command-type") String commandType,
                                                         @RequestBody String jsonCommand) {
        if (commandApi == null) {
            initializeCommandApi();
        }

        String fullCommandClassName = simpleToFullClassName.get(commandType);
        if (fullCommandClassName == null) {
            logger.info("Tried to publish a command[{}] which does not exist.", commandType);
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }


        Object command;
        try {
            command = objectMapper.readValue(jsonCommand, classLoader.loadClass(fullCommandClassName));
        } catch (ClassNotFoundException | IOException e) {
            logger.error("Failed to instantiate command for [{}] and json [{}]", commandType, jsonCommand, e);
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }

        return commandGateway.send(command).thenApply(ResponseEntity::ok);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
