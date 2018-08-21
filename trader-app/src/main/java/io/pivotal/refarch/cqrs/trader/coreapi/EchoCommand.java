package io.pivotal.refarch.cqrs.trader.coreapi;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

//TODO remove this command eventually
public class EchoCommand {

    @TargetAggregateIdentifier
    private final String message;

    public EchoCommand(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
