/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.refarch.cqrs.trader.app.query.portfolio;

import io.pivotal.refarch.cqrs.trader.app.query.orderbook.OrderBookViewRepository;
import io.pivotal.refarch.cqrs.trader.app.query.orders.trades.OrderBookView;
import io.pivotal.refarch.cqrs.trader.app.query.users.UserView;
import io.pivotal.refarch.cqrs.trader.app.query.users.UserViewRepository;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioByUserIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioCreatedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.*;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemReservationCancelledForPortfolioEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemReservationConfirmedForPortfolioEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemsAddedToPortfolioEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemsReservedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * We setup this test with a default portfolio and a default orderBook. The portfolio contains the default amount of
 * items in Reservation. This means that all available items are reserved.
 */
public class PortfolioEventHandlerTest {

    private static final int DEFAULT_AMOUNT_ITEMS = 100;
    private static final int DEFAULT_RESERVED_AMOUNT_OF_MONEY = 1000;
    private static final int DEFAULT_AMOUNT_OF_MONEY = 10000;

    private final PortfolioViewRepository portfolioViewRepository = mock(PortfolioViewRepository.class);
    private final OrderBookViewRepository orderBookViewRepository = mock(OrderBookViewRepository.class);
    private final UserViewRepository userViewRepository = mock(UserViewRepository.class);

    private final UserId userId = new UserId();
    private final OrderBookId itemId = new OrderBookId();
    private final PortfolioId portfolioId = new PortfolioId();
    private final CompanyId companyId = new CompanyId();
    private final TransactionId transactionId = new TransactionId();

    private ArgumentCaptor<PortfolioView> viewCaptor = ArgumentCaptor.forClass(PortfolioView.class);

    private PortfolioEventHandler testSubject;

    @Before
    public void setUp() {
        when(orderBookViewRepository.getOne(itemId.getIdentifier())).thenReturn(buildTestOrderBook());
        when(portfolioViewRepository.getOne(portfolioId.getIdentifier())).thenReturn(buildTestPortfolio());
        when(userViewRepository.getOne(userId.getIdentifier())).thenReturn(buildTestUser());

        testSubject = new PortfolioEventHandler(portfolioViewRepository, orderBookViewRepository, userViewRepository);
    }

    @Test
    public void testOnPortfolioCreatedEventAPortfolioViewIsCreated() {
        when(userViewRepository.findByIdentifier(userId.getIdentifier())).thenReturn(buildTestUser());

        long expectedAmountOfMoney = 0L;
        long expectedAmountOfReservedMoney = 0L;

        testSubject.on(new PortfolioCreatedEvent(portfolioId, userId));

        verify(userViewRepository).findByIdentifier(userId.getIdentifier());
        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        assertEquals(expectedAmountOfMoney, result.getAmountOfMoney());
        assertEquals(expectedAmountOfReservedMoney, result.getReservedAmountOfMoney());
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void testOnItemsAddedToPortfolioEventItemsAreAdded() {
        int expectedNumberOfPossessedItems = 1;
        int expectedAmountOfPossessedItems = 2 * DEFAULT_AMOUNT_ITEMS;
        int expectedNumberOfReservedItems = 1;
        int expectedAmountOfReservedItems = DEFAULT_AMOUNT_ITEMS;

        testSubject.on(new ItemsAddedToPortfolioEvent(portfolioId, itemId, 100));

        verify(orderBookViewRepository).getOne(itemId.getIdentifier());
        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        Map<String, ItemEntry> resultItemsInPossession = result.getItemsInPossession();
        assertEquals(expectedNumberOfPossessedItems, resultItemsInPossession.size());
        assertEquals(expectedAmountOfPossessedItems, resultItemsInPossession.get(itemId.getIdentifier()).getAmount());
        Map<String, ItemEntry> resultItemsReserved = result.getItemsReserved();
        assertEquals(expectedNumberOfReservedItems, resultItemsReserved.size());
        assertEquals(expectedAmountOfReservedItems, resultItemsReserved.get(itemId.getIdentifier()).getAmount());
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void testOnItemsReservedEventThatAnItemIsReserved() {
        int expectedNumberOfPossessedItems = 1;
        int expectedAmountOfPossessedItems = DEFAULT_AMOUNT_ITEMS;
        int expectedNumberOfReservedItems = 1;
        int expectedAmountOfReservedItems = 2 * DEFAULT_AMOUNT_ITEMS;

        testSubject.on(new ItemsReservedEvent(portfolioId, itemId, transactionId, DEFAULT_AMOUNT_ITEMS));

        verify(orderBookViewRepository).getOne(itemId.getIdentifier());
        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        Map<String, ItemEntry> resultItemsInPossession = result.getItemsInPossession();
        assertEquals(expectedNumberOfPossessedItems, resultItemsInPossession.size());
        assertEquals(expectedAmountOfPossessedItems, resultItemsInPossession.get(itemId.getIdentifier()).getAmount());
        Map<String, ItemEntry> resultItemsReserved = result.getItemsReserved();
        assertEquals(expectedNumberOfReservedItems, resultItemsReserved.size());
        assertEquals(expectedAmountOfReservedItems, resultItemsReserved.get(itemId.getIdentifier()).getAmount());
    }

    /**
     * We are going to confirm 50 of the items in the reservation. Therefore we expect the reservation to become 50
     * less than the default amount of items.
     */
    @Test
    public void testOnItemReservationConfirmedForPortfolioEventThatPossessedAndReservedItemsIsLowered() {
        int expectedNumberOfPossessedItems = 1;
        int expectedAmountOfPossessedItems = DEFAULT_AMOUNT_ITEMS - 50;
        int expectedNumberOfReservedItems = 1;
        int expectedAmountOfReservedItems = DEFAULT_AMOUNT_ITEMS - 50;

        testSubject.on(new ItemReservationConfirmedForPortfolioEvent(portfolioId, itemId, transactionId, 50));

        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        Map<String, ItemEntry> resultItemsInPossession = result.getItemsInPossession();
        assertEquals(expectedNumberOfPossessedItems, resultItemsInPossession.size());
        assertEquals(expectedAmountOfPossessedItems, resultItemsInPossession.get(itemId.getIdentifier()).getAmount());
        Map<String, ItemEntry> resultItemsReserved = result.getItemsReserved();
        assertEquals(expectedNumberOfReservedItems, resultItemsReserved.size());
        assertEquals(expectedAmountOfReservedItems, resultItemsReserved.get(itemId.getIdentifier()).getAmount());
    }

    @Test
    public void testOnItemReservationCancelledForPortfolioEventThatItemsAreUnreserved() {
        int expectedNumberOfPossessedItems = 1;
        int expectedAmountOfPossessedItems = 2 * DEFAULT_AMOUNT_ITEMS;
        int expectedNumberOfReservedItems = 0;

        ItemReservationCancelledForPortfolioEvent testEvent =
                new ItemReservationCancelledForPortfolioEvent(portfolioId, itemId, transactionId, DEFAULT_AMOUNT_ITEMS);

        testSubject.on(testEvent);

        verify(orderBookViewRepository).getOne(itemId.getIdentifier());
        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        Map<String, ItemEntry> resultItemsInPossession = result.getItemsInPossession();
        assertEquals(expectedNumberOfPossessedItems, resultItemsInPossession.size());
        assertEquals(expectedAmountOfPossessedItems, resultItemsInPossession.get(itemId.getIdentifier()).getAmount());
        Map<String, ItemEntry> resultItemsReserved = result.getItemsReserved();
        assertEquals(expectedNumberOfReservedItems, resultItemsReserved.size());
        assertNull(resultItemsReserved.get(itemId.getIdentifier()));
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void testOnCashDepositedEventThatPortfolioAmountIsIncreased() {
        long testMoneyAddedInCents = 50L;

        long expectedAmountOfMoney = DEFAULT_AMOUNT_OF_MONEY + testMoneyAddedInCents;
        long expectedAmountOfReservedMoney = DEFAULT_RESERVED_AMOUNT_OF_MONEY;

        testSubject.on(new CashDepositedEvent(portfolioId, testMoneyAddedInCents));

        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        assertEquals(expectedAmountOfMoney, result.getAmountOfMoney());
        assertEquals(expectedAmountOfReservedMoney, result.getReservedAmountOfMoney());
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void testOnCashWithdrawnEventThatPortfolioAmountIsDecreased() {
        long testAmountPaidInCents = 50L;

        long expectedAmountOfMoney = DEFAULT_AMOUNT_OF_MONEY - testAmountPaidInCents;
        long expectedAmountOfReservedMoney = DEFAULT_RESERVED_AMOUNT_OF_MONEY;

        testSubject.on(new CashWithdrawnEvent(portfolioId, testAmountPaidInCents));

        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        assertEquals(expectedAmountOfMoney, result.getAmountOfMoney());
        assertEquals(expectedAmountOfReservedMoney, result.getReservedAmountOfMoney());
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void testOnCashReservedEventThatPortfolioReservedAmountIsIncreased() {
        long testAmountToReserve = 50L;

        long expectedAmountOfMoney = DEFAULT_AMOUNT_OF_MONEY;
        long expectedAmountOfReservedMoney = DEFAULT_RESERVED_AMOUNT_OF_MONEY + testAmountToReserve;

        testSubject.on(new CashReservedEvent(portfolioId, transactionId, testAmountToReserve));

        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        assertEquals(expectedAmountOfMoney, result.getAmountOfMoney());
        assertEquals(expectedAmountOfReservedMoney, result.getReservedAmountOfMoney());
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void testOnCashReservationCancelledEventThatPortfolioReservedAmountIsDecreased() {
        long testAmountOfMoneyToCancel = 50L;

        long expectedAmountOfMoney = DEFAULT_AMOUNT_OF_MONEY;
        long expectedAmountOfReservedMoney = DEFAULT_RESERVED_AMOUNT_OF_MONEY - testAmountOfMoneyToCancel;

        testSubject.on(new CashReservationCancelledEvent(portfolioId, transactionId, testAmountOfMoneyToCancel));

        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        assertEquals(expectedAmountOfMoney, result.getAmountOfMoney());
        assertEquals(expectedAmountOfReservedMoney, result.getReservedAmountOfMoney());
    }

    @Test
    public void testOnCashReservationConfirmedEventThatPortfolioReservedAndOverallAmountAreDecreased() {
        long testAmountOfMoneyConfirmedInCents = 50L;
        CashReservationConfirmedEvent testEvent =
                new CashReservationConfirmedEvent(portfolioId, transactionId, testAmountOfMoneyConfirmedInCents);

        long expectedAmountOfMoney = DEFAULT_AMOUNT_OF_MONEY - testAmountOfMoneyConfirmedInCents;
        long expectedAmountOfReservedMoney = DEFAULT_RESERVED_AMOUNT_OF_MONEY - testAmountOfMoneyConfirmedInCents;

        testSubject.on(testEvent);

        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        assertEquals(expectedAmountOfMoney, result.getAmountOfMoney());
        assertEquals(expectedAmountOfReservedMoney, result.getReservedAmountOfMoney());
    }

    @Test
    public void testFindByPortfolioIdReturnsAPortfolioView() {
        PortfolioView testView = buildTestPortfolio();
        when(portfolioViewRepository.getOne(portfolioId.getIdentifier())).thenReturn(testView);

        PortfolioView result = testSubject.find(new PortfolioByIdQuery(portfolioId));

        assertEquals(testView, result);
    }

    @Test
    public void testFindPortfolioByUserIdReturnsAPortfolioView() {
        PortfolioView testView = buildTestPortfolio();
        when(portfolioViewRepository.findByUserId(userId.getIdentifier())).thenReturn(testView);

        PortfolioView result = testSubject.find(new PortfolioByUserIdQuery(userId));

        assertEquals(testView, result);
    }

    private PortfolioView buildTestPortfolio() {
        PortfolioView portfolioView = new PortfolioView();
        portfolioView.setIdentifier(portfolioId.getIdentifier());
        portfolioView.setUserId(userId.getIdentifier());
        portfolioView.setAmountOfMoney(10000);
        portfolioView.setReservedAmountOfMoney(1000);
        portfolioView.addItemInPossession(buildTestItem(itemId, companyId));
        portfolioView.addReservedItem(buildTestItem(itemId, companyId));
        return portfolioView;
    }

    private ItemEntry buildTestItem(OrderBookId itemIdentifier, CompanyId companyIdentifier) {
        ItemEntry itemInPossession = new ItemEntry();
        itemInPossession.setIdentifier(itemIdentifier.getIdentifier());
        itemInPossession.setCompanyId(companyIdentifier.getIdentifier());
        itemInPossession.setCompanyName("Test company");
        itemInPossession.setAmount(DEFAULT_AMOUNT_ITEMS);
        return itemInPossession;
    }

    private OrderBookView buildTestOrderBook() {
        OrderBookView orderBookView = new OrderBookView();
        orderBookView.setIdentifier(itemId.getIdentifier());
        orderBookView.setCompanyIdentifier(companyId.getIdentifier());
        orderBookView.setCompanyName("Test Company");
        return orderBookView;
    }

    private UserView buildTestUser() {
        UserView testView = new UserView();
        testView.setIdentifier(userId.getIdentifier());
        testView.setUsername("john.doe");
        testView.setPassword("54498159823489s9fd84");
        return testView;
    }
}
