package io.pivotal.refarch.cqrs.trader.app.controller;

import io.pivotal.refarch.cqrs.trader.coreapi.EchoCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/echo")
public class EchoController {

    private final CommandGateway commandGateway;

    private static final Logger LOGGER = getLogger(EchoController.class);
    public EchoController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @GetMapping("/{message}")
    public CompletableFuture<String> say(@PathVariable String message) {
        LOGGER.debug("Creating an Echo Command containing the message [{}]", message);
        return commandGateway.send(new EchoCommand(message));
    }

}
