package io.pivotal.refarch.cqrs.trader.app;

import io.pivotal.refarch.cqrs.trader.app.query.company.CompanyView;
import io.pivotal.refarch.cqrs.trader.app.query.company.CompanyViewRepository;
import io.pivotal.refarch.cqrs.trader.app.query.orderbook.OrderBookViewRepository;
import io.pivotal.refarch.cqrs.trader.app.query.orders.trades.OrderBookView;
import io.pivotal.refarch.cqrs.trader.app.query.portfolio.PortfolioView;
import io.pivotal.refarch.cqrs.trader.app.query.portfolio.PortfolioViewRepository;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CreateCompanyCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.DepositCashCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.AddItemsToPortfolioCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.users.CreateUserCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.TrackingEventStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraderAggregateDataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(TraderAggregateDataInitializer.class);

    private final CommandGateway commandGateway;
    private final EventStore eventStore;
    private final CompanyViewRepository companyRepository;
    private final PortfolioViewRepository portfolioRepository;
    private final OrderBookViewRepository orderBookRepository;

    public TraderAggregateDataInitializer(CommandGateway commandGateway,
                                          EventStore eventStore,
                                          CompanyViewRepository companyRepository,
                                          PortfolioViewRepository portfolioRepository,
                                          OrderBookViewRepository orderBookRepository) {
        this.commandGateway = commandGateway;
        this.eventStore = eventStore;
        this.companyRepository = companyRepository;
        this.portfolioRepository = portfolioRepository;
        this.orderBookRepository = orderBookRepository;
    }

    @SuppressWarnings("unused")
    @EventListener
    public void initializeTraderAggregates(ContextRefreshedEvent event) {
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

    private void createCompanies(UserId userId) {
        commandGateway.sendAndWait(new CreateCompanyCommand(new CompanyId(), userId, "Pivotal", 500, 5000));
        commandGateway.sendAndWait(new CreateCompanyCommand(new CompanyId(), userId, "AxonIQ", 1000, 10000));
    }

    private void addMoney(UserId buyer1, long amount) {
        PortfolioView portfolioView = portfolioRepository.findByUserId(buyer1.getIdentifier());

        commandGateway.sendAndWait(new DepositCashCommand(new PortfolioId(portfolioView.getIdentifier()), amount));
    }

    private void addItems(UserId user, String companyName, long amount) {
        PortfolioView portfolioView = portfolioRepository.findByUserId(user.getIdentifier());
        OrderBookView orderBookView = obtainOrderBookByCompanyName(companyName);

        commandGateway.sendAndWait(new AddItemsToPortfolioCommand(new PortfolioId(portfolioView.getIdentifier()),
                                                                  new OrderBookId(orderBookView.getIdentifier()),
                                                                  amount));
    }

    private OrderBookView obtainOrderBookByCompanyName(String companyName) {
        CompanyView companyView = companyRepository.findByName(companyName);
        if (companyView != null) {
            List<OrderBookView> orderBookEntries =
                    orderBookRepository.findByCompanyIdentifier(companyView.getIdentifier());

            return orderBookEntries.get(0);
        }
        throw new IllegalStateException("Problem initializing, could not find company with required name.");
    }
}