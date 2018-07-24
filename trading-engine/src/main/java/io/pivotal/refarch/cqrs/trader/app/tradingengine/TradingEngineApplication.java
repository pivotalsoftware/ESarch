package io.pivotal.refarch.cqrs.trader.app.tradingengine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class TradingEngineApplication {

    private static final Logger LOG = LoggerFactory.getLogger(TradingEngineApplication.class);

    /*
    * There is not much to do here. Apart from integrating with Rabbit for the incoming events
    * and then handling those events, the rest of the functionality of the view is provided
    * by Spring Data Repositories.
    */

    public static void main(String[] args) {
        SpringApplication.run(TradingEngineApplication.class, args);
    }
}
