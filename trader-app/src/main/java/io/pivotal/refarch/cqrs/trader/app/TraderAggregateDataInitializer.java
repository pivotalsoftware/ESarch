package io.pivotal.refarch.cqrs.trader.app;

import io.pivotal.refarch.cqrs.trader.app.query.company.CompanyView;
import io.pivotal.refarch.cqrs.trader.app.query.orders.trades.OrderBookView;
import io.pivotal.refarch.cqrs.trader.app.query.portfolio.PortfolioView;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyByNameQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CreateCompanyCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderBooksByCompanyIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioByUserIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.DepositCashCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.AddItemsToPortfolioCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.users.CreateUserCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.TrackingEventStream;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.responsetypes.ResponseTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class TraderAggregateDataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(TraderAggregateDataInitializer.class);

    private final CommandGateway commandGateway;
    private final EventStore eventStore;
    private final QueryGateway queryGateway;

    public TraderAggregateDataInitializer(CommandGateway commandGateway,
                                          EventStore eventStore,
                                          QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.eventStore = eventStore;
        this.queryGateway = queryGateway;
    }

    @SuppressWarnings("unused")
    @EventListener
    public void initializeTraderAggregates(InstanceRegisteredEvent event)
            throws InterruptedException, ExecutionException {
        Thread.sleep(60000);
        TrackingEventStream trackingEventStream = eventStore.openStream(null);
        if (trackingEventStream.hasNextAvailable()) {
            logger.info("Verified the Event Store already contains events. "
                                + "This suggests we have already initialized our default Trader Aggregates.");
            return;
        }

        logger.info("Verified the Event Store does not contain any events. "
                            + "Starting initialization of Trader Aggregates.");

        UserId buyer1 = createUser("Buyer One", "buyer1");
        UserId buyer2 = createUser("Buyer Two", "buyer2");
        UserId buyer3 = createUser("Buyer Three", "buyer3");
        UserId buyer4 = createUser("Buyer Four", "buyer4");
        UserId buyer5 = createUser("Buyer Five", "buyer5");
        UserId buyer6 = createUser("Buyer Six", "buyer6");

        createCompanies(buyer1);

        addMoney(buyer1, 100000);
        addItems(buyer2, "Pivotal", 10000L);
        addMoney(buyer3, 100000);
        addItems(buyer4, "Pivotal", 10000L);
        addMoney(buyer5, 100000);
        addItems(buyer6, "AxonIQ", 10000L);
    }

    private UserId createUser(String longName, String userName) {
        UserId userId = new UserId();
        commandGateway.sendAndWait(new CreateUserCommand(userId, longName, userName, userName));
        return userId;
    }

    private void createCompanies() {
        commandGateway.sendAndWait(new CreateCompanyCommand(new CompanyId(), "Pivotal", 500, 5000));
        commandGateway.sendAndWait(new CreateCompanyCommand(new CompanyId(), "AxonIQ", 1000, 10000));
    }

    private void addMoney(UserId userId, long amount) throws ExecutionException, InterruptedException {
        PortfolioView portfolioView = queryGateway.query(new PortfolioByUserIdQuery(userId),
                                                         ResponseTypes.instanceOf(PortfolioView.class)).get();
        commandGateway.sendAndWait(new DepositCashCommand(new PortfolioId(portfolioView.getIdentifier()), amount));
    }

    private void addItems(UserId userId, String companyName, long amount)
            throws ExecutionException, InterruptedException {
        PortfolioView portfolioView = queryGateway.query(new PortfolioByUserIdQuery(userId),
                                                         ResponseTypes.instanceOf(PortfolioView.class)).get();
        OrderBookView orderBookView = obtainOrderBookByCompanyName(companyName);

        commandGateway.sendAndWait(new AddItemsToPortfolioCommand(new PortfolioId(portfolioView.getIdentifier()),
                                                                  new OrderBookId(orderBookView.getIdentifier()),
                                                                  amount));
    }

    private OrderBookView obtainOrderBookByCompanyName(String companyName)
            throws ExecutionException, InterruptedException {
        CompanyView companyView = queryGateway.query(new CompanyByNameQuery(companyName),
                                                     ResponseTypes.instanceOf(CompanyView.class)).get();
        if (companyView != null) {
            OrderBooksByCompanyIdQuery query =
                    new OrderBooksByCompanyIdQuery(new CompanyId(companyView.getIdentifier()));
            List<OrderBookView> orderBookEntries =
                    queryGateway.query(query, ResponseTypes.multipleInstancesOf(OrderBookView.class)).get();

            return orderBookEntries.get(0);
        }
        throw new IllegalStateException("Problem initializing, could not find company with required name.");
    }
}