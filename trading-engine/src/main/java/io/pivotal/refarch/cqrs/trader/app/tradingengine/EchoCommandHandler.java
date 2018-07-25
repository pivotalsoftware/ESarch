package io.pivotal.refarch.cqrs.trader.app.tradingengine;

import io.pivotal.refarch.cqrs.trader.coreapi.EchoCommand;
import org.axonframework.commandhandling.CommandHandler;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;

@Component
public class EchoCommandHandler {

    @CommandHandler
    public String hi(EchoCommand echoCommand) {
        return "Hi I am " + ManagementFactory.getRuntimeMXBean().getName() + " and got your message. You said: " + echoCommand.getMessage();
    }
}
