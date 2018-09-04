package io.pivotal.refarch.cqrs.trader.tradingengine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

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

    /**
     * Instantiate an {@link ObjectMapper} for Jackson de-/serialization.
     * Additionally, a {@link KotlinModule} is registered, as the Commands, Events and Queries are written in Kotlin.
     *
     * @return an {@link ObjectMapper} for Jackson de-/serialization
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new KotlinModule());
        return objectMapper;
    }

    @Bean
    @Qualifier("eventSerializer")
    public Serializer eventSerializer(ObjectMapper objectMapper) {
        return new JacksonSerializer(objectMapper);
    }

}
