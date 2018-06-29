package io.pivotal.catalog.configuration;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpEventPublicationConfiguration {


    @Value("${axon.amqp.exchange:CatalogEvents}")
    String exchangeName;

    //    Add @Bean annotated exchange() method to declare a spring amqp Exchange
    //    Return the exchange from ExchangeBuilder.fanoutExchange(“ComplaintEvents”).build();

    @Bean
    public Exchange exchange(){
        return ExchangeBuilder.fanoutExchange(exchangeName).build();
    }

    //    Add @Bean annotated queue() method to declate a Queue
    //    Return the queue from QueueBuilder.durable(“ComplaintEvents”).build()

    @Bean
    public Queue queue(){
        return QueueBuilder.durable(exchangeName).build();
    }

    //    Add @Bean annotated binding() method to declare a Binding
    //    Return the binding from BindingBuilder.bind(queue()).to(exchange()).with(“*”).noargs()

    @Bean
    public Binding binding(Queue queue, Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("*").noargs();
    }

    //    Add @Autowired method to configure(AmqpAdmin admin)
    //    Make admin.declareExchange(exchange());
    //    Make admin.declareQueue(queue());
    //    Make admin.declareBinding(binding());

    @Autowired
    public void configure(AmqpAdmin amqpAdmin, Exchange exchange, Queue queue, Binding binding){
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(binding);
    }
}
