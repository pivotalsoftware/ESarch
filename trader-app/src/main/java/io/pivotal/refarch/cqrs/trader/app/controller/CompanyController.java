package io.pivotal.refarch.cqrs.trader.app.controller;

import io.pivotal.refarch.cqrs.trader.coreapi.company.CreateCompanyCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Future;

@RestController
@RequestMapping("/company")
public class CompanyController {

    private final CommandGateway commandGateway;

    public CompanyController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public Future<CompanyId> createCompany(@RequestBody String companyName) {
        return commandGateway.send(new CreateCompanyCommand(new CompanyId(), new UserId(), companyName, 1000, 1000));
    }
}
