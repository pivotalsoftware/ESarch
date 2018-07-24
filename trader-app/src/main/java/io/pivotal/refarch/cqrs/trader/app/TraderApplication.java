package io.pivotal.refarch.cqrs.trader.app;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.distributed.DistributedCommandBus;
import org.axonframework.messaging.InterceptorChain;
import org.axonframework.messaging.MessageHandlerInterceptor;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity(debug = true)
@SpringBootApplication
public class TraderApplication {

    private static final Logger LOG = LoggerFactory.getLogger(TraderApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TraderApplication.class, args);
        LOG.info("Starting the COMMAND-SIDE PCF Axon CQRS Demo using SpringBoot.");
    }

    @Autowired
    public void configure(DistributedCommandBus commandBus) {
        commandBus.registerHandlerInterceptor(new MessageHandlerInterceptor<CommandMessage<?>>() {
            @Override
            public Object handle(UnitOfWork<? extends CommandMessage<?>> unitOfWork, InterceptorChain interceptorChain) throws Exception {
                return interceptorChain.proceed();
            }
        });
    }

    @Configuration
    public static class TraderSecurity extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // TODO: Figure this out...
            http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/**").permitAll();
        }
    }
}
