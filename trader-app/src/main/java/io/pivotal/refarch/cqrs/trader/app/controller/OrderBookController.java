package io.pivotal.refarch.cqrs.trader.app.controller;

import io.pivotal.refarch.cqrs.trader.coreapi.company.AddOrderBookToCompanyCommand;
import io.pivotal.refarch.cqrs.trader.app.query.orders.trades.OrderBookView;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.CreateOrderBookCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderBookViewQuery;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.responsetypes.ResponseTypes;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.Future;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/orderbook")
public class OrderBookController {

    private static final Logger LOGGER = getLogger(OrderBookController.class);
    
    private final QueryGateway queryGateway;
    private final CommandGateway commandGateway;

    public OrderBookController(QueryGateway queryGateway, CommandGateway commandGateway) {
        this.queryGateway = queryGateway;
        this.commandGateway = commandGateway;
    }

    @GetMapping("/{companyId}")
    public Future<List<OrderBookView>> getOrderBook(@PathVariable String companyId) {
        LOGGER.debug("Creating OrderBook view query for company [{}]", companyId);
        return queryGateway.query(new OrderBookViewQuery(new CompanyId(companyId)), ResponseTypes.multipleInstancesOf(OrderBookView.class));
    }

    @PostMapping("/{companyId}")
    public Future<OrderBookId> createOrderBook(@PathVariable String companyId) {
        LOGGER.debug("Creating OrderBook for company [{}]", companyId);
        return commandGateway.send(new CreateOrderBookCommand(new OrderBookId()))
                             .thenCompose(orderBookId -> commandGateway.send(new AddOrderBookToCompanyCommand(new CompanyId(companyId), (OrderBookId) orderBookId)));
    }
}
