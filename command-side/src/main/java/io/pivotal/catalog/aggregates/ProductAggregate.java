package io.pivotal.catalog.aggregates;

import io.pivotal.catalog.commands.AddProductToCatalogCommand;
import io.pivotal.catalog.events.ProductAddedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class ProductAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(ProductAggregate.class);

    @AggregateIdentifier
    private String id;
    private String name;

    public ProductAggregate() {
    }

    @CommandHandler
    public ProductAggregate(AddProductToCatalogCommand cmd) {
        LOG.debug("Handling {} command: {}, {}", cmd.getClass().getSimpleName(), cmd.getId(), cmd.getName());
        Assert.hasLength(cmd.getId(), "ID should NOT be empty or null.");
        Assert.hasLength(cmd.getName(), "Name should NOT be empty or null.");
        apply(new ProductAddedEvent(cmd.getId(), cmd.getName()));
        LOG.trace("Done handling {} command: {}, {}", cmd.getClass().getSimpleName(), cmd.getId(), cmd.getName());
    }

    @EventSourcingHandler
    public void on(ProductAddedEvent evnt) {
        LOG.debug("Handling {} event: {}, {}", evnt.getClass().getSimpleName(), evnt.getId(), evnt.getName());
        this.id = evnt.getId();
        this.name = evnt.getName();
        LOG.trace("Done handling {} event: {}, {}", evnt.getClass().getSimpleName(), evnt.getId(), evnt.getName());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
