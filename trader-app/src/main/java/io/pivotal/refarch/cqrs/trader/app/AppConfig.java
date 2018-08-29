package io.pivotal.refarch.cqrs.trader.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.distributed.DistributedCommandBus;
import org.axonframework.messaging.interceptors.LoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    /**
     * We have chosen to take this <i>optional</i> step in order to benefit from automatic logging of every command
     * message being published on to the {@link CommandBus}. These are `INFO` level statements of every action.
     * We have qualified the given CommandBus parameter with 'localSegment' as we are using a
     * {@link DistributedCommandBus} and we want to associate this {@link LoggingInterceptor} with the local CommandBus
     * node.
     *
     * @param commandBus the {@link CommandBus} auto configured for this application
     */
    @Autowired
    public void configure(@Qualifier("localSegment") CommandBus commandBus) {
        commandBus.registerHandlerInterceptor(new LoggingInterceptor<>());
    }

    @Autowired
    public void configure(DistributedCommandBus commandBus) {
        commandBus.registerHandlerInterceptor((unitOfWork, interceptorChain) -> interceptorChain.proceed());
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public JsonSchemaGenerator jsonSchemaGenerator(ObjectMapper objectMapper) {
        return new JsonSchemaGenerator(objectMapper);
    }
}
