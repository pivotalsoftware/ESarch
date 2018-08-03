package io.pivotal.refarch.cqrs.trader.tradingengine;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfiguration {

    @Bean
    public Exchange eventsExchange() {
        return ExchangeBuilder.topicExchange("trading-engine-events").build();
    }

    @Autowired
    public void defineExchange(AmqpAdmin admin) {
        admin.declareExchange(eventsExchange());
    }

}
