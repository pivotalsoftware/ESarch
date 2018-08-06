package io.pivotal.refarch.cqrs.trader.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@SpringBootApplication
public class TraderApplication {

    private static final Logger LOG = LoggerFactory.getLogger(TraderApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TraderApplication.class, args);
        LOG.info("Started the Axon CQRS and Event Sourcing Trader-app.");
    }
}
