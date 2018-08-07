package io.pivotal.refarch.cqrs.trader.app;

import org.axonframework.commandhandling.distributed.CommandRouter;
import org.axonframework.commandhandling.distributed.ConsistentHash;
import org.axonframework.commandhandling.distributed.ConsistentHashChangeListener;
import org.axonframework.commandhandling.distributed.RoutingStrategy;
import org.axonframework.serialization.Serializer;
import org.axonframework.springcloud.commandhandling.SpringCloudCommandRouter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Endpoint(id = "distributed-command-bus")
public class DistributedCommandBusConfig {

    private final AtomicReference<ConsistentHash> consistentHash = new AtomicReference<>();

    @Autowired
    private Serializer serializer;

    @ReadOperation
    public String consistentHash() {
        return serializer.serialize(consistentHash.get().getMembers(), String.class).getData();
    }

    @Bean
    public ConsistentHashChangeListener consistentHashChangeListener() {
        return consistentHash::set;
    }

    @Bean
    public CommandRouter springCloudCommandRouter(DiscoveryClient discoveryClient,
                                                  Registration localServiceInstance,
                                                  RoutingStrategy routingStrategy,
                                                  ConsistentHashChangeListener chcListener) {
        return new SpringCloudCommandRouter(discoveryClient, localServiceInstance, routingStrategy, Objects::nonNull,
                                            chcListener);
    }

}
