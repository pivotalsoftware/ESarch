package io.pivotal.refarch.cqrs.trader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableDiscoveryClient
@SpringBootApplication
public class TraderApplication {

    private static final Logger LOG = LoggerFactory.getLogger(TraderApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TraderApplication.class, args);
        LOG.info("Starting the COMMAND-SIDE PCF Axon CQRS Demo using SpringBoot.");
    }
}
