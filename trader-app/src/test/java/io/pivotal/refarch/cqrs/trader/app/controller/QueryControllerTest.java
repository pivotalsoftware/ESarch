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
import org.axonframework.queryhandling.responsetypes.InstanceResponseType;
import org.axonframework.queryhandling.responsetypes.MultipleInstancesResponseType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.singletonList;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

// Suppresses unchecked generic on InstanceResponseType and MultipleInstancesResponseType
@SuppressWarnings("unchecked")
public class QueryControllerTest {

    private final QueryGateway queryGateway = mock(QueryGateway.class);

    private QueryController testSubject;

    @Before
    public void setUp() {
        testSubject = new QueryController(queryGateway);
    }

    @Test
    public void testGetCompanyByIdReturnsCompany() throws Exception {
        CompanyView expectedView = new CompanyView();

        String testCompanyId = "some-company-id";
        CompanyByIdQuery testQuery = new CompanyByIdQuery(new CompanyId(testCompanyId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<CompanyView> result = testSubject.getCompanyById(testCompanyId);

        assertTrue(result.isDone());
        assertEquals(expectedView, result.get());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testFindAllCompaniesReturnsListOfCompany() throws Exception {
        CompanyView expectedView = new CompanyView();

        FindAllCompaniesQuery testQuery = new FindAllCompaniesQuery(0, 50);
        when(queryGateway.query(eq(testQuery), any(MultipleInstancesResponseType.class)))
                .thenReturn(completedFuture(singletonList(expectedView)));

        CompletableFuture<List<CompanyView>> result = testSubject.findAllCompanies(0, 50);

        assertTrue(result.isDone());
        assertEquals(expectedView, result.get().get(0));
        verify(queryGateway).query(eq(testQuery), any(MultipleInstancesResponseType.class));
    }

    @Test
    public void testGetOrderBookByIdReturnsOrderBook() throws Exception {
        OrderBookView expectedView = new OrderBookView();

        String testOrderBookId = "some-order-book-id";
        OrderBookByIdQuery testQuery = new OrderBookByIdQuery(new OrderBookId(testOrderBookId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<OrderBookView> result = testSubject.getOrderBookById(testOrderBookId);

        assertTrue(result.isDone());
        assertEquals(expectedView, result.get());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testGetOrderBooksByCompanyIdReturnsListOfOrderBook() throws Exception {
        List<OrderBookView> expectedView = singletonList(new OrderBookView());

        String testCompanyId = "some-company-id";
        OrderBooksByCompanyIdQuery testQuery = new OrderBooksByCompanyIdQuery(new CompanyId(testCompanyId));
        when(queryGateway.query(eq(testQuery), any(MultipleInstancesResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<List<OrderBookView>> result = testSubject.getOrderBooksByCompanyId(testCompanyId);

        assertTrue(result.isDone());
        assertEquals(expectedView, result.get());
        verify(queryGateway).query(eq(testQuery), any(MultipleInstancesResponseType.class));
    }

    @Test
    public void testGetTransactionByIdReturnsTransaction() throws Exception {
        TransactionView expectedView = new TransactionView();

        String testTransactionId = "some-transaction-id";
        TransactionByIdQuery testQuery = new TransactionByIdQuery(new TransactionId(testTransactionId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<TransactionView> result = testSubject.getTransactionById(testTransactionId);

        assertTrue(result.isDone());
        assertEquals(expectedView, result.get());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testGetTransactionsByPortfolioIdReturnsListOfTransaction()
            throws Exception {
        List<TransactionView> expectedView = singletonList(new TransactionView());

        String testPortfolioId = "some-portfolio-id";
        TransactionsByPortfolioIdQuery testQuery = new TransactionsByPortfolioIdQuery(new PortfolioId(testPortfolioId));
        when(queryGateway.query(eq(testQuery), any(MultipleInstancesResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<List<TransactionView>> result = testSubject.getTransactionsByPortfolioId(testPortfolioId);

        assertTrue(result.isDone());
        assertEquals(expectedView, result.get());
        verify(queryGateway).query(eq(testQuery), any(MultipleInstancesResponseType.class));
    }

    @Test
    public void testGetExecutedTradesByOrderBookIdReturnsListOfTradeExecuted()
            throws Exception {
        List<TradeExecutedView> expectedView = singletonList(new TradeExecutedView());

        String testOrderBookId = "some-order-book-id";
        ExecutedTradesByOrderBookIdQuery testQuery =
                new ExecutedTradesByOrderBookIdQuery(new OrderBookId(testOrderBookId));
        when(queryGateway.query(eq(testQuery), any(MultipleInstancesResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<List<TradeExecutedView>> result = testSubject.getExecutedTradesByOrderBookId(testOrderBookId);

        assertTrue(result.isDone());
        assertEquals(expectedView, result.get());
        verify(queryGateway).query(eq(testQuery), any(MultipleInstancesResponseType.class));
    }

    @Test
    public void testGetPortfolioByIdReturnsPortfolio() throws Exception {
        PortfolioView expectedView = new PortfolioView();

        String testPortfolioId = "some-portfolio-id";
        PortfolioByIdQuery testQuery = new PortfolioByIdQuery(new PortfolioId(testPortfolioId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<PortfolioView> result = testSubject.getPortfolioById(testPortfolioId);

        assertTrue(result.isDone());
        assertEquals(expectedView, result.get());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testGetPortfolioByUserIdReturnsPortfolio() throws Exception {
        PortfolioView expectedView = new PortfolioView();

        String testUserId = "some-user-id";
        PortfolioByUserIdQuery testQuery = new PortfolioByUserIdQuery(new UserId(testUserId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<PortfolioView> result = testSubject.getPortfolioByUserId(testUserId);

        assertTrue(result.isDone());
        assertEquals(expectedView, result.get());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testGetUserByIdReturnsUser() throws Exception {
        UserView expectedView = new UserView();

        String testUserId = "some-user-id";
        UserByIdQuery testQuery = new UserByIdQuery(new UserId(testUserId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<UserView> result = testSubject.getUserById(testUserId);

        assertTrue(result.isDone());
        assertEquals(expectedView, result.get());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testFindAllUsersReturnsUserList() throws Exception {
        UserView expectedView = new UserView();

        FindAllUsersQuery testQuery = new FindAllUsersQuery(0, 50);
        when(queryGateway.query(eq(testQuery), any(MultipleInstancesResponseType.class)))
                .thenReturn(completedFuture(singletonList(expectedView)));

        CompletableFuture<List<UserView>> result = testSubject.findAllUsers(0, 50);

        assertTrue(result.isDone());
        assertEquals(expectedView, result.get().get(0));
        verify(queryGateway).query(eq(testQuery), any(MultipleInstancesResponseType.class));
    }
}
