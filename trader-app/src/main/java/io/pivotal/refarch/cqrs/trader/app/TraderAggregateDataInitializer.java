package io.pivotal.refarch.cqrs.trader.app;

import io.pivotal.refarch.cqrs.trader.app.query.orders.trades.OrderBookView;
import io.pivotal.refarch.cqrs.trader.app.query.portfolio.PortfolioView;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CreateCompanyCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderBooksByCompanyIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.CreatePortfolioCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioByUserIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.DepositCashCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.AddItemsToPortfolioCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.users.CreateUserCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.distributed.CommandRouter;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.TrackingEventStream;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.responsetypes.ResponseType;
import org.axonframework.queryhandling.responsetypes.ResponseTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.axonframework.commandhandling.GenericCommandMessage.asCommandMessage;

@Endpoint(id = "data-initializer")
@Component
public class TraderAggregateDataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(TraderAggregateDataInitializer.class);

    private final CommandGateway commandGateway;
    private final EventStore eventStore;
    private final QueryGateway queryGateway;
    private final CommandRouter commandRouter;

    public TraderAggregateDataInitializer(CommandGateway commandGateway,
                                          EventStore eventStore,
                                          QueryGateway queryGateway,
                                          CommandRouter commandRouter) {
        this.commandGateway = commandGateway;
        this.eventStore = eventStore;
        this.queryGateway = queryGateway;
        this.commandRouter = commandRouter;
    }

    @ReadOperation
    public boolean hasData() {
        try (TrackingEventStream trackingEventStream = eventStore.openStream(null)) {
            return trackingEventStream.hasNextAvailable();
        }
    }

    @WriteOperation
    public boolean initializeTraderAggregates()
            throws InterruptedException, ExecutionException {
        if (hasData()) {
            logger.info("Verified the Event Store already contains events. Aborting initialization...");
            return true;
        }
        logger.info("Verified the Event Store does not contain any events. "
                            + "Starting initialization of Trader Aggregates.");

        CommandMessage<Object> canary = asCommandMessage(new CreatePortfolioCommand(new PortfolioId(), new UserId()));
        if (!commandRouter.findDestination(canary).isPresent()) {
            logger.info("Trading-Engine isn't running. Unable to initialize data");
            return false;
        }

        CompanyId pivotalId = new CompanyId();
        CompanyId axonIQId = new CompanyId();
        commandGateway.sendAndWait(new CreateCompanyCommand(pivotalId, "Pivotal", 500, 5000));
        commandGateway.sendAndWait(new CreateCompanyCommand(axonIQId, "AxonIQ", 1000, 10000));

        UserId buyer1 = createUser("Buyer One", "buyer1");
        UserId buyer2 = createUser("Buyer Two", "buyer2");
        UserId buyer3 = createUser("Buyer Three", "buyer3");
        UserId buyer4 = createUser("Buyer Four", "buyer4");
        UserId buyer5 = createUser("Buyer Five", "buyer5");
        UserId buyer6 = createUser("Buyer Six", "buyer6");

        addMoney(buyer1, 100000);
        addItems(buyer2, pivotalId, 10000L);
        addMoney(buyer3, 100000);
        addItems(buyer4, axonIQId, 10000L);
        addMoney(buyer5, 100000);
        addItems(buyer6, axonIQId, 10000L);

        return true;
    }

    private UserId createUser(String longName, String userName) {
        UserId userId = new UserId();
        commandGateway.sendAndWait(new CreateUserCommand(userId, longName, userName, userName));
        return userId;
    }

    private void addMoney(UserId userId, long amount) throws ExecutionException, InterruptedException {
        PortfolioView portfolioView = queryNonNull(new PortfolioByUserIdQuery(userId),
                                                   ResponseTypes.instanceOf(PortfolioView.class));

        commandGateway.sendAndWait(new DepositCashCommand(new PortfolioId(portfolioView.getIdentifier()), amount));
    }

    private void addItems(UserId userId, CompanyId companyId, long amount)
            throws ExecutionException, InterruptedException {
        PortfolioView portfolioView = queryNonNull(new PortfolioByUserIdQuery(userId),
                                                   ResponseTypes.instanceOf(PortfolioView.class));
        OrderBookView orderBookView = obtainOrderBookByCompanyId(companyId);

        commandGateway.sendAndWait(new AddItemsToPortfolioCommand(new PortfolioId(portfolioView.getIdentifier()),
                                                                  new OrderBookId(orderBookView.getIdentifier()),
                                                                  amount));
    }

    private OrderBookView obtainOrderBookByCompanyId(CompanyId companyId)
            throws ExecutionException, InterruptedException {
        OrderBooksByCompanyIdQuery query =
                new OrderBooksByCompanyIdQuery(companyId);
        List<OrderBookView> orderBookEntries =
                queryGateway.query(query, ResponseTypes.multipleInstancesOf(OrderBookView.class)).get();

        return orderBookEntries.get(0);
    }

    public <Q, R> R queryNonNull(Q query, ResponseType<R> responseType) throws ExecutionException, InterruptedException {
        R result = queryGateway.query(query, responseType).get();
        for (int i = 0; i < 20 && result == null; i++) {
            Thread.sleep(500);
            result = queryGateway.query(query, responseType).get();
            if (i == 19 && result == null) {
                throw new RuntimeException("Unable to initialize, not getting any value for " + query.getClass().getSimpleName());
            }
        }
        return result;
    }
}
