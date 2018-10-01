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
import org.junit.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.singletonList;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.junit.Assert.*;
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

        CompletableFuture<ResponseEntity<CompanyView>> result = testSubject.getCompanyById(testCompanyId);

        assertTrue(result.isDone());
        ResponseEntity<CompanyView> responseEntity = result.get();
        assertSame(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedView, responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testGetCompanyByIdReturnsNotFoundForNonExistingId() throws Exception {
        String testCompanyId = "non-existing-id";
        CompanyByIdQuery testQuery = new CompanyByIdQuery(new CompanyId(testCompanyId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(null));

        CompletableFuture<ResponseEntity<CompanyView>> result = testSubject.getCompanyById(testCompanyId);

        assertTrue(result.isDone());
        ResponseEntity<CompanyView> responseEntity = result.get();
        assertSame(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testFindAllCompaniesReturnsListOfCompany() throws Exception {
        CompanyView expectedView = new CompanyView();

        FindAllCompaniesQuery testQuery = new FindAllCompaniesQuery(0, 50);
        when(queryGateway.query(eq(testQuery), any(MultipleInstancesResponseType.class)))
                .thenReturn(completedFuture(singletonList(expectedView)));

        CompletableFuture<ResponseEntity<List<CompanyView>>> result = testSubject.findAllCompanies(0, 50);

        assertTrue(result.isDone());
        ResponseEntity<List<CompanyView>> responseEntity = result.get();
        assertSame(HttpStatus.OK, responseEntity.getStatusCode());
        List<CompanyView> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(expectedView, responseBody.get(0));
        verify(queryGateway).query(eq(testQuery), any(MultipleInstancesResponseType.class));
    }

    @Test
    public void testGetOrderBookByIdReturnsOrderBook() throws Exception {
        OrderBookView expectedView = new OrderBookView();

        String testOrderBookId = "some-order-book-id";
        OrderBookByIdQuery testQuery = new OrderBookByIdQuery(new OrderBookId(testOrderBookId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<ResponseEntity<OrderBookView>> result = testSubject.getOrderBookById(testOrderBookId);

        assertTrue(result.isDone());
        ResponseEntity<OrderBookView> responseEntity = result.get();
        assertSame(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedView, responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testGetOrderBookByIdReturnsNotFoundForNonExistingId() throws Exception {
        String testOrderBookId = "non-existing-id";
        OrderBookByIdQuery testQuery = new OrderBookByIdQuery(new OrderBookId(testOrderBookId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(null));

        CompletableFuture<ResponseEntity<OrderBookView>> result = testSubject.getOrderBookById(testOrderBookId);

        assertTrue(result.isDone());
        ResponseEntity<OrderBookView> responseEntity = result.get();
        assertSame(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testGetOrderBooksByCompanyIdReturnsListOfOrderBook() throws Exception {
        List<OrderBookView> expectedView = singletonList(new OrderBookView());

        String testCompanyId = "some-company-id";
        OrderBooksByCompanyIdQuery testQuery = new OrderBooksByCompanyIdQuery(new CompanyId(testCompanyId));
        when(queryGateway.query(eq(testQuery), any(MultipleInstancesResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<ResponseEntity<List<OrderBookView>>> result =
                testSubject.getOrderBooksByCompanyId(testCompanyId);

        assertTrue(result.isDone());
        ResponseEntity<List<OrderBookView>> responseEntity = result.get();
        assertSame(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedView, responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(MultipleInstancesResponseType.class));
    }

    @Test
    public void testGetOrderBooksByCompanyIdReturnsNotFoundForNonExistingId() throws Exception {
        String testCompanyId = "non-existing-id";
        OrderBooksByCompanyIdQuery testQuery = new OrderBooksByCompanyIdQuery(new CompanyId(testCompanyId));
        when(queryGateway.query(eq(testQuery), any(MultipleInstancesResponseType.class)))
                .thenReturn(completedFuture(Collections.emptyList()));

        CompletableFuture<ResponseEntity<List<OrderBookView>>> result =
                testSubject.getOrderBooksByCompanyId(testCompanyId);

        assertTrue(result.isDone());
        ResponseEntity<List<OrderBookView>> responseEntity = result.get();
        assertSame(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(MultipleInstancesResponseType.class));
    }

    @Test
    public void testGetTransactionByIdReturnsTransaction() throws Exception {
        TransactionView expectedView = new TransactionView();

        String testTransactionId = "some-transaction-id";
        TransactionByIdQuery testQuery = new TransactionByIdQuery(new TransactionId(testTransactionId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<ResponseEntity<TransactionView>> result = testSubject.getTransactionById(testTransactionId);

        assertTrue(result.isDone());
        ResponseEntity<TransactionView> responseEntity = result.get();
        assertSame(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedView, responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testGetTransactionByIdReturnsNotFoundForNonExistingId() throws Exception {
        String testTransactionId = "non-existing-id";
        TransactionByIdQuery testQuery = new TransactionByIdQuery(new TransactionId(testTransactionId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(null));

        CompletableFuture<ResponseEntity<TransactionView>> result = testSubject.getTransactionById(testTransactionId);

        assertTrue(result.isDone());
        ResponseEntity<TransactionView> responseEntity = result.get();
        assertSame(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testGetTransactionsByPortfolioIdReturnsListOfTransaction() throws Exception {
        List<TransactionView> expectedView = singletonList(new TransactionView());

        String testPortfolioId = "some-portfolio-id";
        TransactionsByPortfolioIdQuery testQuery = new TransactionsByPortfolioIdQuery(new PortfolioId(testPortfolioId));
        when(queryGateway.query(eq(testQuery), any(MultipleInstancesResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<ResponseEntity<List<TransactionView>>> result =
                testSubject.getTransactionsByPortfolioId(testPortfolioId);

        assertTrue(result.isDone());
        ResponseEntity<List<TransactionView>> responseEntity = result.get();
        assertSame(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedView, responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(MultipleInstancesResponseType.class));
    }

    @Test
    public void testGetTransactionsByPortfolioIdReturnsNotFoundForNonExistingId() throws Exception {
        String testPortfolioId = "non-existing-id";
        TransactionsByPortfolioIdQuery testQuery = new TransactionsByPortfolioIdQuery(new PortfolioId(testPortfolioId));
        when(queryGateway.query(eq(testQuery), any(MultipleInstancesResponseType.class)))
                .thenReturn(completedFuture(Collections.emptyList()));

        CompletableFuture<ResponseEntity<List<TransactionView>>> result =
                testSubject.getTransactionsByPortfolioId(testPortfolioId);

        assertTrue(result.isDone());
        ResponseEntity<List<TransactionView>> responseEntity = result.get();
        assertSame(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(MultipleInstancesResponseType.class));
    }

    @Test
    public void testGetExecutedTradesByOrderBookIdReturnsListOfTradeExecuted() throws Exception {
        List<TradeExecutedView> expectedView = singletonList(new TradeExecutedView());

        String testOrderBookId = "some-order-book-id";
        ExecutedTradesByOrderBookIdQuery testQuery =
                new ExecutedTradesByOrderBookIdQuery(new OrderBookId(testOrderBookId));
        when(queryGateway.query(eq(testQuery), any(MultipleInstancesResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<ResponseEntity<List<TradeExecutedView>>> result =
                testSubject.getExecutedTradesByOrderBookId(testOrderBookId);

        assertTrue(result.isDone());
        ResponseEntity<List<TradeExecutedView>> responseEntity = result.get();
        assertSame(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedView, responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(MultipleInstancesResponseType.class));
    }

    @Test
    public void testGetExecutedTradesByOrderBookIdReturnsNotFoundForNonExistingId() throws Exception {
        String testOrderBookId = "non-existing-id";
        ExecutedTradesByOrderBookIdQuery testQuery =
                new ExecutedTradesByOrderBookIdQuery(new OrderBookId(testOrderBookId));
        when(queryGateway.query(eq(testQuery), any(MultipleInstancesResponseType.class)))
                .thenReturn(completedFuture(Collections.emptyList()));

        CompletableFuture<ResponseEntity<List<TradeExecutedView>>> result =
                testSubject.getExecutedTradesByOrderBookId(testOrderBookId);

        assertTrue(result.isDone());
        ResponseEntity<List<TradeExecutedView>> responseEntity = result.get();
        assertSame(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(MultipleInstancesResponseType.class));
    }

    @Test
    public void testGetPortfolioByIdReturnsPortfolio() throws Exception {
        PortfolioView expectedView = new PortfolioView();

        String testPortfolioId = "some-portfolio-id";
        PortfolioByIdQuery testQuery = new PortfolioByIdQuery(new PortfolioId(testPortfolioId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<ResponseEntity<PortfolioView>> result = testSubject.getPortfolioById(testPortfolioId);

        assertTrue(result.isDone());
        ResponseEntity<PortfolioView> responseEntity = result.get();
        assertSame(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedView, responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testGetPortfolioByIdReturnsNotFoundForNonExistingId() throws Exception {
        String testPortfolioId = "non-existing-id";
        PortfolioByIdQuery testQuery = new PortfolioByIdQuery(new PortfolioId(testPortfolioId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(null));

        CompletableFuture<ResponseEntity<PortfolioView>> result = testSubject.getPortfolioById(testPortfolioId);

        assertTrue(result.isDone());
        ResponseEntity<PortfolioView> responseEntity = result.get();
        assertSame(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testGetPortfolioByUserIdReturnsPortfolio() throws Exception {
        PortfolioView expectedView = new PortfolioView();

        String testUserId = "some-user-id";
        PortfolioByUserIdQuery testQuery = new PortfolioByUserIdQuery(new UserId(testUserId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<ResponseEntity<PortfolioView>> result = testSubject.getPortfolioByUserId(testUserId);

        assertTrue(result.isDone());
        ResponseEntity<PortfolioView> responseEntity = result.get();
        assertSame(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedView, responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testGetPortfolioByUserIdReturnsNotFoundForNonExistingId() throws Exception {
        String testUserId = "non-existing-id";
        PortfolioByUserIdQuery testQuery = new PortfolioByUserIdQuery(new UserId(testUserId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(null));

        CompletableFuture<ResponseEntity<PortfolioView>> result = testSubject.getPortfolioByUserId(testUserId);

        assertTrue(result.isDone());
        ResponseEntity<PortfolioView> responseEntity = result.get();
        assertSame(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testGetUserByIdReturnsUser() throws Exception {
        UserView expectedView = new UserView();

        String testUserId = "some-user-id";
        UserByIdQuery testQuery = new UserByIdQuery(new UserId(testUserId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(expectedView));

        CompletableFuture<ResponseEntity<UserView>> result = testSubject.getUserById(testUserId);

        assertTrue(result.isDone());
        ResponseEntity<UserView> responseEntity = result.get();
        assertSame(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedView, responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testGetUserByIdReturnsNotFoundForNonExistingId() throws Exception {
        String testUserId = "non-existing-id";
        UserByIdQuery testQuery = new UserByIdQuery(new UserId(testUserId));
        when(queryGateway.query(eq(testQuery), any(InstanceResponseType.class)))
                .thenReturn(completedFuture(null));

        CompletableFuture<ResponseEntity<UserView>> result = testSubject.getUserById(testUserId);

        assertTrue(result.isDone());
        ResponseEntity<UserView> responseEntity = result.get();
        assertSame(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(queryGateway).query(eq(testQuery), any(InstanceResponseType.class));
    }

    @Test
    public void testFindAllUsersReturnsUserList() throws Exception {
        UserView expectedView = new UserView();

        FindAllUsersQuery testQuery = new FindAllUsersQuery(0, 50);
        when(queryGateway.query(eq(testQuery), any(MultipleInstancesResponseType.class)))
                .thenReturn(completedFuture(singletonList(expectedView)));

        CompletableFuture<ResponseEntity<List<UserView>>> result = testSubject.findAllUsers(0, 50);

        assertTrue(result.isDone());
        ResponseEntity<List<UserView>> responseEntity = result.get();
        assertSame(HttpStatus.OK, responseEntity.getStatusCode());
        List<UserView> responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(expectedView, responseBody.get(0));
        verify(queryGateway).query(eq(testQuery), any(MultipleInstancesResponseType.class));
    }
}
