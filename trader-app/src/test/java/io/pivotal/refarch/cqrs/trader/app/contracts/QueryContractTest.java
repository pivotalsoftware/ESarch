package io.pivotal.refarch.cqrs.trader.app.contracts;

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
import io.pivotal.refarch.cqrs.trader.coreapi.company.FindAllCompaniesQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.TransactionType;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderBookByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderBooksByCompanyIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.ExecutedTradesByOrderBookIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionState;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionsByPortfolioIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioByUserIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.users.FindAllUsersQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserByIdQuery;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.responsetypes.InstanceResponseType;
import org.axonframework.queryhandling.responsetypes.MultipleInstancesResponseType;
import org.jetbrains.annotations.NotNull;
import org.junit.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class QueryContractTest {

    private static final String ORDER_BOOK_ID = "f82c40ec-a785-11e8-98d0-529269fb1459";
    private static final String TRANSACTION_ID = "f82c448e-a785-11e8-98d0-529269fb1459";
    private static final String PORTFOLIO_ID = "f82c481c-a785-11e8-98d0-529269fb1459";
    private static final String BUY_ORDER_ID = "f82c4984-a785-11e8-98d0-529269fb1459";
    private static final String SELL_ORDER_ID = "f82c4ace-a785-11e8-98d0-529269fb1459";
    private static final String COMPANY_ID = "f82c40ec-a785-11e8-98d0-529269fb1459";
    private static final String COMPANY_NAME = "Pivotal";
    private static final String USER_ID = "98684ad8-987e-4d16-8ad8-b620f4320f4c";
    private static final String USER_NAME = "Pieter Humphrey";

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        final QueryGateway queryGateway = mock(QueryGateway.class);

        // Default to an empty CompletableFuture for InstanceResponseType queries - specify working queries later
        when(queryGateway.query(any(), any(InstanceResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(null));
        // Default to a empty List CompletableFuture for MultipleInstancesResponseType queries - specify working queries later
        when(queryGateway.query(any(), any(MultipleInstancesResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(Collections.emptyList()));

        when(queryGateway.query(argThat(this::matchCompanyByIdQuery), any(InstanceResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(buildCompanyView()));
        when(queryGateway.query(any(FindAllCompaniesQuery.class), any(MultipleInstancesResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(buildAllCompaniesView()));
        when(queryGateway.query(argThat(this::matchOrderBookByIdQuery), any(InstanceResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(buildOrderBookView()));
        when(queryGateway.query(argThat(this::matchOrderBooksByCompanyIdQuery),
                                any(MultipleInstancesResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(Collections.singletonList(buildOrderBookView())));
        when(queryGateway.query(argThat(this::matchTransactionByIdQuery), any(InstanceResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(buildTransactionView()));
        when(queryGateway.query(argThat(this::matchTransactionsByPortfolioIdQuery),
                                any(MultipleInstancesResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(Collections.singletonList(buildTransactionView())));
        when(queryGateway.query(argThat(this::matchExecutedTradesByOrderBookIdQuery),
                                any(MultipleInstancesResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(Collections.singletonList(buildTradeExecutedView())));
        when(queryGateway.query(argThat(this::matchPortfolioByIdQuery), any(InstanceResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(buildPortfolioView()));
        when(queryGateway.query(argThat(this::matchPortfolioByUserIdQuery), any(InstanceResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(buildPortfolioView()));
        when(queryGateway.query(argThat(this::matchUserByIdQuery), any(InstanceResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(buildUserView()));
        when(queryGateway.query(any(FindAllUsersQuery.class), any(MultipleInstancesResponseType.class)))
                .thenReturn(CompletableFuture.completedFuture(buildAllUsersView()));

        final QueryController queryController = new QueryController(queryGateway);

        RestAssuredMockMvc.standaloneSetup(queryController);
    }

    private <Q> boolean matchCompanyByIdQuery(Q query) {
        return query instanceof CompanyByIdQuery
                && ((CompanyByIdQuery) query).getCompanyId().getIdentifier().equals(COMPANY_ID);
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

    private List<CompanyView> buildAllCompaniesView() {
        return Collections.singletonList(buildCompanyView());
    }

    private <Q> boolean matchOrderBookByIdQuery(Q query) {
        return query instanceof OrderBookByIdQuery
                && ((OrderBookByIdQuery) query).getOrderBookId().getIdentifier().equals(ORDER_BOOK_ID);
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

    private <Q> boolean matchOrderBooksByCompanyIdQuery(Q query) {
        return query instanceof OrderBooksByCompanyIdQuery
                && ((OrderBooksByCompanyIdQuery) query).getCompanyId().getIdentifier().equals(COMPANY_ID);
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

    private <Q> boolean matchTransactionByIdQuery(Q query) {
        return query instanceof TransactionByIdQuery
                && ((TransactionByIdQuery) query).getTransactionId().getIdentifier().equals(TRANSACTION_ID);
    }

    private <Q> boolean matchTransactionsByPortfolioIdQuery(Q query) {
        return query instanceof TransactionsByPortfolioIdQuery
                && ((TransactionsByPortfolioIdQuery) query).getPortfolioId().getIdentifier().equals(PORTFOLIO_ID);
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

    private <Q> boolean matchExecutedTradesByOrderBookIdQuery(Q query) {
        return query instanceof ExecutedTradesByOrderBookIdQuery
                && ((ExecutedTradesByOrderBookIdQuery) query).getOrderBookId().getIdentifier().equals(ORDER_BOOK_ID);
    }

    @NotNull
    private PortfolioView buildPortfolioView() {
        PortfolioView portfolioView = new PortfolioView();
        portfolioView.setIdentifier(PORTFOLIO_ID);
        portfolioView.setUserId(USER_ID);
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

    private <Q> boolean matchPortfolioByIdQuery(Q query) {
        return query instanceof PortfolioByIdQuery
                && ((PortfolioByIdQuery) query).getPortfolioId().getIdentifier().equals(PORTFOLIO_ID);
    }

    private <Q> boolean matchPortfolioByUserIdQuery(Q query) {
        return query instanceof PortfolioByUserIdQuery
                && ((PortfolioByUserIdQuery) query).getUserId().getIdentifier().equals(USER_ID);
    }

    @NotNull
    private UserView buildUserView() {
        UserView userView = new UserView();
        userView.setIdentifier(USER_ID);
        userView.setName(USER_NAME);
        userView.setUsername("john.doe");
        return userView;
    }

    private <Q> boolean matchUserByIdQuery(Q query) {
        return query instanceof UserByIdQuery
                && ((UserByIdQuery) query).getUserId().getIdentifier().equals(USER_ID);
    }

    @NotNull
    private List<UserView> buildAllUsersView() {
        return Collections.singletonList(buildUserView());
    }
}
