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
      logger.warn("#### The Event Store already contains events. Aborting initialization...");
      return true;
    }
    logger.info("#### The Event Store does not contain any events. "
            + "Starting initialization of Trader Aggregates.");

    CommandMessage<Object> canary = asCommandMessage(new CreatePortfolioCommand(new PortfolioId(), new UserId()));
    if (!commandRouter.findDestination(canary).isPresent()) {
      logger.error("Trading-Engine isn't running. Unable to initialize data");
      return false;
    }

    logger.info("Adding some companies to the application...");
    CompanyId pivotalId = new CompanyId();
    CompanyId axonIQId = new CompanyId();
    CompanyId solsticeId = new CompanyId();
    commandGateway.sendAndWait(new CreateCompanyCommand(pivotalId, "Pivotal", 100000000, 1000000));
    commandGateway.sendAndWait(new CreateCompanyCommand(axonIQId, "AxonIQ", 100000000, 1000000));
    commandGateway.sendAndWait(new CreateCompanyCommand(solsticeId, "Solstice", 100000000, 1000000));

    logger.info("Adding some users to the application...");
    UserId allard = createUser("Allard", "allard");
    UserId steven = createUser("Steven", "steven");
    UserId ben = createUser("Ben", "ben");
    UserId pieter = createUser("Pieter", "pieter");
    UserId sampath = createUser("Sampath", "sampath");
    UserId haridu = createUser("Haridu", "haridu");
    UserId jakub = createUser("Jakub", "jakub");
    UserId kenny = createUser("Kenny", "kenny");
    UserId david = createUser("David", "david");

    logger.info("Giving each user a starting position...");
    addMoney(allard, 100000);
    addItems(allard, axonIQId, 10000L);

    addMoney(steven, 100000);
    addItems(steven, axonIQId, 10000L);

    addMoney(ben, 100000);
    addItems(ben, pivotalId, 10000L);

    addMoney(pieter, 100000);
    addItems(pieter, pivotalId, 10000L);

    addMoney(sampath, 100000);
    addItems(sampath, solsticeId, 10000L);

    addMoney(haridu, 100000);
    addItems(haridu, solsticeId, 10000L);

    addMoney(jakub, 100000);
    addItems(jakub, pivotalId, 10000L);

    addMoney(kenny, 100000);
    addItems(kenny, pivotalId, 10000L);

    addMoney(david, 100000);
    addItems(david, pivotalId, 10000L);

    logger.info("All done. Data initialisation is complete.");

    return true;
  }

  private UserId createUser(String longName, String userName) {
    logger.debug("createUser() called with: longName = [" + longName + "], userName = [" + userName + "]");
    UserId userId = new UserId();
    commandGateway.sendAndWait(new CreateUserCommand(userId, longName, userName, userName));
    return userId;
  }

  private void addMoney(UserId userId, long amount) throws ExecutionException, InterruptedException {
    logger.debug("addMoney() called with: userId = [" + userId + "], amount = [" + amount + "]");
    PortfolioView portfolioView = queryNonNull(new PortfolioByUserIdQuery(userId),
            ResponseTypes.instanceOf(PortfolioView.class));

    commandGateway.sendAndWait(new DepositCashCommand(new PortfolioId(portfolioView.getIdentifier()), amount));
  }

  private void addItems(UserId userId, CompanyId companyId, long amount)
          throws ExecutionException, InterruptedException {
    logger.debug("addItems() called with: userId = [" + userId + "], companyId = [" + companyId + "], amount = [" + amount + "]");
    PortfolioView portfolioView = queryNonNull(new PortfolioByUserIdQuery(userId),
            ResponseTypes.instanceOf(PortfolioView.class));
    OrderBookView orderBookView = obtainOrderBookByCompanyId(companyId);

    commandGateway.sendAndWait(new AddItemsToPortfolioCommand(new PortfolioId(portfolioView.getIdentifier()),
            new OrderBookId(orderBookView.getIdentifier()),
            amount));
  }

  private OrderBookView obtainOrderBookByCompanyId(CompanyId companyId)
          throws ExecutionException, InterruptedException {
    logger.debug("obtainOrderBookByCompanyId() called with: companyId = [" + companyId + "]");
    OrderBooksByCompanyIdQuery query =
            new OrderBooksByCompanyIdQuery(companyId);
    List<OrderBookView> orderBookEntries =
            queryGateway.query(query, ResponseTypes.multipleInstancesOf(OrderBookView.class)).get();

    return orderBookEntries.get(0);
  }

  public <Q, R> R queryNonNull(Q query, ResponseType<R> responseType) throws ExecutionException, InterruptedException {
    logger.debug("queryNonNull() called with: query = [" + query + "], responseType = [" + responseType + "]");
    R result = queryGateway.query(query, responseType).get();
    for (int i = 0; i < 30 && result == null; i++) {
      logger.debug("Trying...");
      Thread.sleep(2000);
      result = queryGateway.query(query, responseType).get();
      if (i == 29 && result == null) {
        logger.error("Tried many times, now giving up!");
        throw new RuntimeException("Unable to continue initialisation. Not getting any value for " + query.getClass().getSimpleName());
      }
    }

    logger.debug("Successfully obtained result for ({}): {}", query.getClass().getSimpleName(), result);
    return result;
  }
}
