package io.pivotal.refarch.cqrs.trader.app;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.AMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.SubscribableMessageSource;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfiguration {

    @Autowired
    public void defineExchange(AmqpAdmin admin) {
        Queue tradesQueue = QueueBuilder.durable("trades").build();
        Exchange tradingExchange = ExchangeBuilder.topicExchange("trading-engine-events").build();

        admin.declareExchange(tradingExchange);
        admin.declareQueue(tradesQueue);
        admin.declareBinding(BindingBuilder.bind(tradesQueue)
                                           .to(tradingExchange)
                                           .with("#")
                                           .noargs());
    }

    @Autowired
    public void config(EventProcessingConfiguration epConfig,
                       SubscribableMessageSource<EventMessage<?>> tradeEvents) {
        epConfig.registerSubscribingEventProcessor("trading", c -> tradeEvents);
    }

    @Bean
    public SubscribableMessageSource<EventMessage<?>> tradeEvents(AMQPMessageConverter messageConverter) {
        return new SpringAMQPMessageSource(messageConverter) {
            @RabbitListener(queues = "trades")
            @Override
            public void onMessage(Message message, Channel channel) {
                super.onMessage(message, channel);
            }
        };
    }

}
