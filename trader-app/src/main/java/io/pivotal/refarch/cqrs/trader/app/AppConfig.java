package io.pivotal.refarch.cqrs.trader.app;

import org.axonframework.commandhandling.distributed.DistributedCommandBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Autowired
    public void configure(DistributedCommandBus commandBus) {
        commandBus.registerHandlerInterceptor((unitOfWork, interceptorChain) -> interceptorChain.proceed());
    }
}
