package io.pivotal.refarch.cqrs.trader.controller;

import io.pivotal.refarch.cqrs.trader.coreapi.company.AddOrderBookToCompanyCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderBookView;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderBookViewQuery;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.responsetypes.ResponseTypes;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.CreateOrderBookCommand;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/orderbook")
public class OrderBookController {

    private final QueryGateway queryGateway;
    private final CommandGateway commandGateway;

    public OrderBookController(QueryGateway queryGateway, CommandGateway commandGateway) {
        this.queryGateway = queryGateway;
        this.commandGateway = commandGateway;
    }

    @GetMapping("/{companyId}")
    public Future<List<OrderBookView>> getOrderBook(@PathVariable String companyId) {
        return queryGateway.query(new OrderBookViewQuery(new CompanyId(companyId)), ResponseTypes.multipleInstancesOf(OrderBookView.class));
    }

    @PostMapping("/{companyId}")
    public Future<OrderBookId> createOrderBook(@PathVariable String companyId) {
        return commandGateway.send(new CreateOrderBookCommand(new OrderBookId()))
        .thenCompose(orderBookId -> commandGateway.send(new AddOrderBookToCompanyCommand(new CompanyId(companyId), (OrderBookId) orderBookId)));
    }
}
