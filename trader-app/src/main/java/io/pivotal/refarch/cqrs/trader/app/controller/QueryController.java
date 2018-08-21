package io.pivotal.refarch.cqrs.trader.app.controller;

import io.pivotal.refarch.cqrs.trader.app.query.company.CompanyView;
import io.pivotal.refarch.cqrs.trader.app.query.orders.trades.OrderBookView;
import io.pivotal.refarch.cqrs.trader.app.query.orders.transaction.TradeExecutedView;
import io.pivotal.refarch.cqrs.trader.app.query.orders.transaction.TransactionView;
import io.pivotal.refarch.cqrs.trader.app.query.portfolio.PortfolioView;
import io.pivotal.refarch.cqrs.trader.app.query.users.UserView;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderBookByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderBooksByCompanyIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.ExecutedTradesByOrderBookIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionsByPortfolioIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioByUserIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserByNameQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.responsetypes.ResponseTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/query")
public class QueryController {

    private final QueryGateway queryGateway;

    public QueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping("/company/{companyId}")
    public CompletableFuture<CompanyView> getCompanyById(@RequestParam String companyId) {
        return queryGateway.query(new CompanyByIdQuery(new CompanyId(companyId)),
                                  ResponseTypes.instanceOf(CompanyView.class));
    }

    @GetMapping("/order-book/{orderBookId}")
    public CompletableFuture<OrderBookView> getOrderBookById(@RequestParam String orderBookId) {
        return queryGateway.query(new OrderBookByIdQuery(new OrderBookId(orderBookId)),
                                  ResponseTypes.instanceOf(OrderBookView.class));
    }

    @GetMapping("/order-book/by-company/{companyId}")
    public CompletableFuture<List<OrderBookView>> getOrderBooksByCompanyId(@RequestParam String companyId) {
        return queryGateway.query(new OrderBooksByCompanyIdQuery(new CompanyId(companyId)),
                                  ResponseTypes.multipleInstancesOf(OrderBookView.class));
    }

    @GetMapping("/transaction/{transactionId}")
    public CompletableFuture<TransactionView> getTransactionById(@RequestParam String transactionId) {
        return queryGateway.query(new TransactionByIdQuery(new TransactionId(transactionId)),
                                  ResponseTypes.instanceOf(TransactionView.class));
    }

    @GetMapping("/transaction/by-portfolio/{portfolioId}")
    public CompletableFuture<List<TransactionView>> getTransactionsByPortfolioId(@RequestParam String portfolioId) {
        return queryGateway.query(new TransactionsByPortfolioIdQuery(new PortfolioId(portfolioId)),
                                  ResponseTypes.multipleInstancesOf(TransactionView.class));
    }

    @GetMapping("/executed-trades/{orderBookId}")
    public CompletableFuture<List<TradeExecutedView>> getExecutedTradesByOrderBookId(@RequestParam String orderBookId) {
        return queryGateway.query(new ExecutedTradesByOrderBookIdQuery(new OrderBookId(orderBookId)),
                                  ResponseTypes.multipleInstancesOf(TradeExecutedView.class));
    }

    @GetMapping("/portfolio/{portfolioId}")
    public CompletableFuture<PortfolioView> getPortfolioById(@RequestParam String portfolioId) {
        return queryGateway.query(new PortfolioByIdQuery(new PortfolioId(portfolioId)),
                                  ResponseTypes.instanceOf(PortfolioView.class));
    }

    @GetMapping("/portfolio/by-user/{userId}")
    public CompletableFuture<PortfolioView> getPortfolioByUserId(@RequestParam String userId) {
        return queryGateway.query(new PortfolioByUserIdQuery(new UserId(userId)),
                                  ResponseTypes.instanceOf(PortfolioView.class));
    }

    @GetMapping("/user/{userId}")
    public CompletableFuture<UserView> getUserById(@RequestParam String userId) {
        return queryGateway.query(new UserByIdQuery(new UserId(userId)), ResponseTypes.instanceOf(UserView.class));
    }

    @GetMapping("/user/by-name/{userName}")
    public CompletableFuture<UserView> getUserByName(@RequestParam String userName) {
        return queryGateway.query(new UserByNameQuery(userName), ResponseTypes.instanceOf(UserView.class));
    }
}
