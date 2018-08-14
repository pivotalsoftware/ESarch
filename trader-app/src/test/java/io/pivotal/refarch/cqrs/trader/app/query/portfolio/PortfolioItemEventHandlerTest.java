/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemReservationCancelledForPortfolioEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemReservationConfirmedForPortfolioEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemsAddedToPortfolioEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemsReservedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.junit.*;
import org.mockito.*;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * We setup this test with a default portfolio and a default orderBook. The portfolio contains the default amount of
 * items in Reservation. This means that all available items are reserved.
 */
public class PortfolioItemEventHandlerTest {

    private static final int DEFAULT_AMOUNT_ITEMS = 100;
    private static final String USERNAME = "Super Rich User";

    private final PortfolioViewRepository portfolioViewRepository = mock(PortfolioViewRepository.class);
    private final OrderBookViewRepository orderBookViewRepository = mock(OrderBookViewRepository.class);

    private final UserId userId = new UserId();
    private final OrderBookId itemId = new OrderBookId();
    private final PortfolioId portfolioId = new PortfolioId();
    private final CompanyId companyId = new CompanyId();
    private final TransactionId transactionId = new TransactionId();

    private ArgumentCaptor<PortfolioView> viewCaptor = ArgumentCaptor.forClass(PortfolioView.class);

    private PortfolioItemEventHandler testSubject;

    @Before
    public void setUp() {
        when(orderBookViewRepository.getOne(itemId.getIdentifier())).thenReturn(buildTestOrderBook());
        when(portfolioViewRepository.getOne(portfolioId.getIdentifier())).thenReturn(buildTestPortfolio());

        testSubject = new PortfolioItemEventHandler(portfolioViewRepository, orderBookViewRepository);
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
        assertEquals(USERNAME, result.getUsername());
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
        assertEquals(USERNAME, result.getUsername());
        Map<String, ItemEntry> resultItemsInPossession = result.getItemsInPossession();
        assertEquals(expectedNumberOfPossessedItems, resultItemsInPossession.size());
        assertEquals(expectedAmountOfPossessedItems, resultItemsInPossession.get(itemId.getIdentifier()).getAmount());
        Map<String, ItemEntry> resultItemsReserved = result.getItemsReserved();
        assertEquals(expectedNumberOfReservedItems, resultItemsReserved.size());
        assertNull(resultItemsReserved.get(itemId.getIdentifier()));
    }

    /**
     * We are going to confirm 50 of the items in the reservation. Therefore we expect the reservation to become 50
     * less than the default amount of items.
     */
    @Test
    public void testOnItemReservationConfirmedForPortfolioEventThatPossessedAndReserverdItemsIsLowered() {
        int expectedNumberOfPossessedItems = 1;
        int expectedAmountOfPossessedItems = DEFAULT_AMOUNT_ITEMS - 50;
        int expectedNumberOfReservedItems = 1;
        int expectedAmountOfReservedItems = DEFAULT_AMOUNT_ITEMS - 50;

        testSubject.on(new ItemReservationConfirmedForPortfolioEvent(portfolioId, itemId, transactionId, 50));

        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        assertEquals(USERNAME, result.getUsername());
        Map<String, ItemEntry> resultItemsInPossession = result.getItemsInPossession();
        assertEquals(expectedNumberOfPossessedItems, resultItemsInPossession.size());
        assertEquals(expectedAmountOfPossessedItems, resultItemsInPossession.get(itemId.getIdentifier()).getAmount());
        Map<String, ItemEntry> resultItemsReserved = result.getItemsReserved();
        assertEquals(expectedNumberOfReservedItems, resultItemsReserved.size());
        assertEquals(expectedAmountOfReservedItems, resultItemsReserved.get(itemId.getIdentifier()).getAmount());
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void testOnItemsReservedEventThatAnItemIsReserverd() {
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
        assertEquals(USERNAME, result.getUsername());
        Map<String, ItemEntry> resultItemsInPossession = result.getItemsInPossession();
        assertEquals(expectedNumberOfPossessedItems, resultItemsInPossession.size());
        assertEquals(expectedAmountOfPossessedItems, resultItemsInPossession.get(itemId.getIdentifier()).getAmount());
        Map<String, ItemEntry> resultItemsReserved = result.getItemsReserved();
        assertEquals(expectedNumberOfReservedItems, resultItemsReserved.size());
        assertEquals(expectedAmountOfReservedItems, resultItemsReserved.get(itemId.getIdentifier()).getAmount());
    }

    @SuppressWarnings("Duplicates")
    private PortfolioView buildTestPortfolio() {
        PortfolioView portfolioView = new PortfolioView();
        portfolioView.setIdentifier(portfolioId.getIdentifier());
        portfolioView.setUserId(userId.getIdentifier());
        portfolioView.setUsername(USERNAME);
        portfolioView.setAmountOfMoney(10000);
        portfolioView.setReservedAmountOfMoney(1000);
        portfolioView.addItemInPossession(buildTestItem(itemId, companyId));
        portfolioView.addReservedItem(buildTestItem(itemId, companyId));
        return portfolioView;
    }

    @SuppressWarnings("Duplicates")
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
}
