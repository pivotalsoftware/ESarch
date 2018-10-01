package io.pivotal.refarch.cqrs.trader.app.controller;

import io.pivotal.refarch.cqrs.trader.app.query.company.CompanyView;
import io.pivotal.refarch.cqrs.trader.app.query.orders.trades.OrderBookView;
import io.pivotal.refarch.cqrs.trader.app.query.orders.transaction.TradeExecutedView;
import io.pivotal.refarch.cqrs.trader.app.query.orders.transaction.TransactionView;
import io.pivotal.refarch.cqrs.trader.app.query.portfolio.PortfolioView;
import io.pivotal.refarch.cqrs.trader.app.query.users.UserView;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.company.FindAllCompaniesQuery;
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
import io.pivotal.refarch.cqrs.trader.coreapi.users.FindAllUsersQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.axonframework.queryhandling.responsetypes.ResponseTypes;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@CrossOrigin("*")
@RestController
@RequestMapping("/query")
public class QueryController {

    private final QueryGateway queryGateway;

    public QueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping("/company/{companyId}")
    public CompletableFuture<ResponseEntity<CompanyView>> getCompanyById(@PathVariable String companyId) {
        return queryGateway.query(new CompanyByIdQuery(new CompanyId(companyId)),
                                  ResponseTypes.instanceOf(CompanyView.class))
                           .thenApply(this::wrapResult);
    }

    @GetMapping("/company")
    public CompletableFuture<ResponseEntity<List<CompanyView>>> findAllCompanies(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "100") int pageSize) {
        return queryGateway.query(new FindAllCompaniesQuery(page, pageSize),
                                  ResponseTypes.multipleInstancesOf(CompanyView.class))
                           .thenApply(this::wrapResultList);
    }

    @GetMapping("/order-book/{orderBookId}")
    public CompletableFuture<ResponseEntity<OrderBookView>> getOrderBookById(@PathVariable String orderBookId) {
        return queryGateway.query(new OrderBookByIdQuery(new OrderBookId(orderBookId)),
                                  ResponseTypes.instanceOf(OrderBookView.class))
                           .thenApply(this::wrapResult);
    }

    @GetMapping("/order-book/by-company/{companyId}")
    public CompletableFuture<ResponseEntity<List<OrderBookView>>> getOrderBooksByCompanyId(
            @PathVariable String companyId) {
        return queryGateway.query(new OrderBooksByCompanyIdQuery(new CompanyId(companyId)),
                                  ResponseTypes.multipleInstancesOf(OrderBookView.class))
                           .thenApply(this::wrapResultList);
    }

    @GetMapping("/transaction/{transactionId}")
    public CompletableFuture<ResponseEntity<TransactionView>> getTransactionById(@PathVariable String transactionId) {
        return queryGateway.query(new TransactionByIdQuery(new TransactionId(transactionId)),
                                  ResponseTypes.instanceOf(TransactionView.class))
                           .thenApply(this::wrapResult);
    }

    @GetMapping("/transaction/by-portfolio/{portfolioId}")
    public CompletableFuture<ResponseEntity<List<TransactionView>>> getTransactionsByPortfolioId(
            @PathVariable String portfolioId) {
        return queryGateway.query(new TransactionsByPortfolioIdQuery(new PortfolioId(portfolioId)),
                                  ResponseTypes.multipleInstancesOf(TransactionView.class))
                           .thenApply(this::wrapResultList);
    }

    @GetMapping("/executed-trades/{orderBookId}")
    public CompletableFuture<ResponseEntity<List<TradeExecutedView>>> getExecutedTradesByOrderBookId(
            @PathVariable String orderBookId) {
        return queryGateway.query(new ExecutedTradesByOrderBookIdQuery(new OrderBookId(orderBookId)),
                                  ResponseTypes.multipleInstancesOf(TradeExecutedView.class))
                           .thenApply(this::wrapResultList);
    }

    @GetMapping("/portfolio/{portfolioId}")
    public CompletableFuture<ResponseEntity<PortfolioView>> getPortfolioById(@PathVariable String portfolioId) {
        return queryGateway.query(new PortfolioByIdQuery(new PortfolioId(portfolioId)),
                                  ResponseTypes.instanceOf(PortfolioView.class))
                           .thenApply(this::wrapResult);
    }

    @GetMapping("/portfolio/by-user/{userId}")
    public CompletableFuture<ResponseEntity<PortfolioView>> getPortfolioByUserId(@PathVariable String userId) {
        return queryGateway.query(new PortfolioByUserIdQuery(new UserId(userId)),
                                  ResponseTypes.instanceOf(PortfolioView.class))
                           .thenApply(this::wrapResult);
    }

    @GetMapping("/user/{userId}")
    public CompletableFuture<ResponseEntity<UserView>> getUserById(@PathVariable String userId) {
        return queryGateway.query(new UserByIdQuery(new UserId(userId)), ResponseTypes.instanceOf(UserView.class))
                           .thenApply(this::wrapResult);
    }

    @GetMapping("/user")
    public CompletableFuture<ResponseEntity<List<UserView>>> findAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "100") int pageSize) {
        return queryGateway.query(new FindAllUsersQuery(page, pageSize),
                                  ResponseTypes.multipleInstancesOf(UserView.class))
                           .thenApply(this::wrapResultList);
    }

    @GetMapping("/subscribe/order-book/{orderBookId}")
    public Flux<ServerSentEvent<OrderBookView>> subscribeToOrderBook(@PathVariable String orderBookId) {
        SubscriptionQueryResult<OrderBookView, OrderBookView> subscription = queryGateway
                .subscriptionQuery(new OrderBookByIdQuery(new OrderBookId(orderBookId)),
                                   ResponseTypes.instanceOf(OrderBookView.class),
                                   ResponseTypes.instanceOf(OrderBookView.class));
        return subscription.initialResult().concatWith(subscription.updates())
                           .map(ob -> ServerSentEvent.builder(ob).build());
    }

    @NotNull
    private <T> ResponseEntity<T> wrapResult(T result) {
        return wrapResult(Objects::isNull, result);
    }

    @NotNull
    private <T> ResponseEntity<List<T>> wrapResultList(List<T> result) {
        return wrapResult(resultList -> Objects.isNull(resultList) || resultList.isEmpty(), result);
    }


    @NotNull
    private <T> ResponseEntity<T> wrapResult(Predicate<T> assertResult, T result) {
        return assertResult.test(result) ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }
}
