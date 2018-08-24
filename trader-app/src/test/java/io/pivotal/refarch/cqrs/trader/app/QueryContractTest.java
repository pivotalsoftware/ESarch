package io.pivotal.refarch.cqrs.trader.app;

import io.pivotal.refarch.cqrs.trader.app.controller.QueryController;
import io.pivotal.refarch.cqrs.trader.app.query.company.CompanyView;
import io.pivotal.refarch.cqrs.trader.app.query.orders.trades.OrderBookView;
import io.pivotal.refarch.cqrs.trader.app.query.orders.trades.OrderView;
import io.pivotal.refarch.cqrs.trader.app.query.orders.transaction.TradeExecutedView;
import io.pivotal.refarch.cqrs.trader.app.query.orders.transaction.TransactionView;
import io.pivotal.refarch.cqrs.trader.app.query.portfolio.ItemEntry;
import io.pivotal.refarch.cqrs.trader.app.query.portfolio.PortfolioView;
import io.pivotal.refarch.cqrs.trader.app.query.users.UserView;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.TransactionType;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderBookByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderBooksByCompanyIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.ExecutedTradesByOrderBookIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionState;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionsByPortfolioIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioByUserIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserByNameQuery;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.responsetypes.InstanceResponseType;
import org.axonframework.queryhandling.responsetypes.MultipleInstancesResponseType;
import org.jetbrains.annotations.NotNull;
import org.junit.*;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class QueryContractTest {

    private static final String ORDER_BOOK_ID = UUID.randomUUID().toString();
    private static final String TRANSACTION_ID = UUID.randomUUID().toString();
    private static final String PORTFOLIO_ID = UUID.randomUUID().toString();
    private static final String BUY_ORDER_ID = UUID.randomUUID().toString();
    private static final String SELL_ORDER_ID = UUID.randomUUID().toString();
    private static final String COMPANY_ID = UUID.randomUUID().toString();
    private static final String COMPANY_NAME = "Pivotal";
    private static final String USER_ID = UUID.randomUUID().toString();
    private static final String USER_NAME = "Pieter Humphrey";

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        final QueryGateway queryGateway = mock(QueryGateway.class);

        when(queryGateway.query(any(CompanyByIdQuery.class), any(InstanceResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(buildCompanyView()));
        when(queryGateway.query(any(OrderBookByIdQuery.class), any(InstanceResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(buildOrderBookView()));
        when(queryGateway.query(any(OrderBooksByCompanyIdQuery.class), any(MultipleInstancesResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(Collections.singletonList(buildOrderBookView())));
        when(queryGateway.query(any(TransactionByIdQuery.class), any(InstanceResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(buildTransactionView()));
        when(queryGateway.query(any(TransactionsByPortfolioIdQuery.class), any(MultipleInstancesResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(Collections.singletonList(buildTransactionView())));
        when(queryGateway.query(any(ExecutedTradesByOrderBookIdQuery.class), any(MultipleInstancesResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(Collections.singletonList(buildTradeExecutedView())));
        when(queryGateway.query(any(PortfolioByIdQuery.class), any(InstanceResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(buildPortfolioView()));
        when(queryGateway.query(any(PortfolioByUserIdQuery.class), any(InstanceResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(buildPortfolioView()));
        when(queryGateway.query(any(UserByIdQuery.class), any(InstanceResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(buildUserView()));
        when(queryGateway.query(any(UserByNameQuery.class), any(InstanceResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(buildUserView()));

        final QueryController queryController = new QueryController(queryGateway);

        RestAssuredMockMvc.standaloneSetup(queryController);
    }

    @NotNull
    private CompanyView buildCompanyView() {
        CompanyView companyView = new CompanyView();
        companyView.setIdentifier(COMPANY_ID);
        companyView.setName(COMPANY_NAME);
        companyView.setAmountOfShares(42L);
        companyView.setValue(1337L);
        companyView.setTradeStarted(false);
        return companyView;
    }

    @NotNull
    private OrderBookView buildOrderBookView() {
        OrderBookView orderBookView = new OrderBookView();
        orderBookView.setIdentifier(ORDER_BOOK_ID);
        orderBookView.setCompanyName(COMPANY_NAME);
        orderBookView.setCompanyIdentifier(COMPANY_ID);
        orderBookView.setBuyOrders(Collections.singletonList(buildBuyOrder()));
        orderBookView.setSellOrders(Collections.singletonList(buildSellOrder()));
        return orderBookView;
    }

    @NotNull
    private OrderView buildBuyOrder() {
        OrderView buyOrder = new OrderView();
        buyOrder.setJpaId(1L);
        buyOrder.setIdentifier(BUY_ORDER_ID);
        buyOrder.setUserId(USER_ID);
        buyOrder.setItemPrice(50L);
        buyOrder.setItemsRemaining(10L);
        buyOrder.setType("Buy");
        buyOrder.setTradeCount(5L);
        return buyOrder;
    }

    @NotNull
    private OrderView buildSellOrder() {
        OrderView sellOrder = new OrderView();
        sellOrder.setJpaId(2L);
        sellOrder.setIdentifier(SELL_ORDER_ID);
        sellOrder.setUserId(USER_ID);
        sellOrder.setItemPrice(75L);
        sellOrder.setItemsRemaining(20L);
        sellOrder.setType("Sell");
        sellOrder.setTradeCount(5L);
        return sellOrder;
    }


    @NotNull
    private TransactionView buildTransactionView() {
        TransactionView transactionView = new TransactionView();
        transactionView.setIdentifier(TRANSACTION_ID);
        transactionView.setOrderBookId(ORDER_BOOK_ID);
        transactionView.setPortfolioId(PORTFOLIO_ID);
        transactionView.setCompanyName(COMPANY_NAME);
        transactionView.setType(TransactionType.BUY);
        transactionView.setState(TransactionState.CONFIRMED);
        transactionView.setAmountOfItems(50L);
        transactionView.setAmountOfExecutedItems(25L);
        transactionView.setPricePerItem(123L);
        return transactionView;
    }

    @NotNull
    private TradeExecutedView buildTradeExecutedView() {
        TradeExecutedView tradeExecutedView = new TradeExecutedView();
        tradeExecutedView.setGeneratedId(1L);
        tradeExecutedView.setOrderBookId(ORDER_BOOK_ID);
        tradeExecutedView.setCompanyName(COMPANY_NAME);
        tradeExecutedView.setTradeCount(30L);
        tradeExecutedView.setTradePrice(180L);
        return tradeExecutedView;
    }

    @NotNull
    private PortfolioView buildPortfolioView() {
        PortfolioView portfolioView = new PortfolioView();
        portfolioView.setIdentifier(PORTFOLIO_ID);
        portfolioView.setUserId(USER_ID);
        portfolioView.setUserName(USER_NAME);
        portfolioView.setAmountOfMoney(1_000_000L);
        portfolioView.setReservedAmountOfMoney(5000L);
        portfolioView.addItemInPossession(buildItemInPossession());
        portfolioView.addReservedItem(buildReservedItem());
        return portfolioView;
    }

    @NotNull
    private ItemEntry buildItemInPossession() {
        ItemEntry itemEntry = new ItemEntry();
        itemEntry.setGeneratedId(1L);
        itemEntry.setCompanyId(COMPANY_ID);
        itemEntry.setCompanyName(COMPANY_NAME);
        itemEntry.setIdentifier(ORDER_BOOK_ID);
        itemEntry.setAmount(321L);
        return itemEntry;
    }

    @NotNull
    private ItemEntry buildReservedItem() {
        ItemEntry itemEntry = new ItemEntry();
        itemEntry.setGeneratedId(2L);
        itemEntry.setCompanyId(COMPANY_ID);
        itemEntry.setCompanyName(COMPANY_NAME);
        itemEntry.setIdentifier(ORDER_BOOK_ID);
        itemEntry.setAmount(654L);
        return itemEntry;
    }


    @NotNull
    private UserView buildUserView() {
        UserView userView = new UserView();
        userView.setIdentifier(USER_ID);
        userView.setName(USER_NAME);
        userView.setUsername("john.doe");
        userView.setPassword("something-difficult");
        return userView;
    }
}
