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

package io.pivotal.refarch.cqrs.trader.app.query.transaction;

import io.pivotal.refarch.cqrs.trader.app.query.orderbook.OrderBookViewRepository;
import io.pivotal.refarch.cqrs.trader.app.query.orders.trades.OrderBookView;
import io.pivotal.refarch.cqrs.trader.app.query.orders.transaction.TransactionView;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.TransactionType;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.BuyTransactionCancelledEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.BuyTransactionConfirmedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.BuyTransactionExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.BuyTransactionPartiallyExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.BuyTransactionStartedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionCancelledEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionConfirmedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionPartiallyExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionStartedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionState;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionsByPortfolioIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import org.junit.*;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;

import static io.pivotal.refarch.cqrs.trader.coreapi.orders.TransactionType.SELL;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TransactionEventHandlerTest {

    private static final int DEFAULT_TOTAL_ITEMS = 100;
    private static final int DEFAULT_ITEM_PRICE = 10;
    private static final String DEFAULT_COMPANY_NAME = "Test Company";

    private final OrderBookViewRepository orderBookViewRepository = mock(OrderBookViewRepository.class);
    private final TransactionViewRepository transactionViewRepository = mock(TransactionViewRepository.class);

    private TransactionEventHandler testSubject;

    private final TransactionId transactionId = new TransactionId();
    private final OrderBookId orderBookId = new OrderBookId();
    private final PortfolioId portfolioId = new PortfolioId();
    private final CompanyId companyId = new CompanyId();


    private ArgumentCaptor<TransactionView> transactionViewCaptor = ArgumentCaptor.forClass(TransactionView.class);

    @Before
    public void setUp() {
        OrderBookView orderBookView = new OrderBookView();
        orderBookView.setIdentifier(orderBookId.toString());
        orderBookView.setCompanyIdentifier(companyId.toString());
        orderBookView.setCompanyName(DEFAULT_COMPANY_NAME);
        when(orderBookViewRepository.getOne(orderBookId.getIdentifier())).thenReturn(orderBookView);

        TransactionView transactionView = new TransactionView();
        transactionView.setIdentifier(transactionId.toString());
        transactionView.setAmountOfExecutedItems(0);
        transactionView.setPricePerItem(DEFAULT_ITEM_PRICE);
        transactionView.setState(TransactionState.CANCELLED);
        transactionView.setAmountOfItems(DEFAULT_TOTAL_ITEMS);
        transactionView.setCompanyName(DEFAULT_COMPANY_NAME);
        transactionView.setOrderBookId(orderBookId.toString());
        transactionView.setPortfolioId(portfolioId.toString());
        transactionView.setType(SELL);
        when(transactionViewRepository.getOne(transactionId.getIdentifier())).thenReturn(transactionView);

        testSubject = new TransactionEventHandler(orderBookViewRepository, transactionViewRepository);
    }

    @Test
    public void testOnBuyTransactionStartedEventSavesTransactionView() {
        int expectedAmountOfExecutedItems = 0;

        BuyTransactionStartedEvent testEvent = new BuyTransactionStartedEvent(
                transactionId, orderBookId, portfolioId, DEFAULT_TOTAL_ITEMS, DEFAULT_ITEM_PRICE
        );
        testSubject.on(testEvent);

        verify(transactionViewRepository).save(transactionViewCaptor.capture());

        TransactionView result = transactionViewCaptor.getValue();
        assertEquals(transactionId.getIdentifier(), result.getIdentifier());
        assertEquals(orderBookId.getIdentifier(), result.getOrderBookId());
        assertEquals(portfolioId.getIdentifier(), result.getPortfolioId());
        assertEquals(DEFAULT_COMPANY_NAME, result.getCompanyName());
        assertEquals(DEFAULT_TOTAL_ITEMS, result.getAmountOfItems());
        assertEquals(expectedAmountOfExecutedItems, result.getAmountOfExecutedItems());
        assertEquals(DEFAULT_ITEM_PRICE, result.getPricePerItem());
        assertEquals(TransactionState.STARTED, result.getState());
        assertEquals(TransactionType.BUY, result.getType());
    }

    @Test
    public void testOnSellTransactionStartedEventSavesTransactionView() {
        int expectedAmountOfExecutedItems = 0;

        SellTransactionStartedEvent testEvent = new SellTransactionStartedEvent(
                transactionId, orderBookId, portfolioId, DEFAULT_TOTAL_ITEMS, DEFAULT_ITEM_PRICE
        );
        testSubject.on(testEvent);

        verify(transactionViewRepository).save(transactionViewCaptor.capture());

        TransactionView result = transactionViewCaptor.getValue();
        assertEquals(transactionId.getIdentifier(), result.getIdentifier());
        assertEquals(orderBookId.getIdentifier(), result.getOrderBookId());
        assertEquals(portfolioId.getIdentifier(), result.getPortfolioId());
        assertEquals(DEFAULT_COMPANY_NAME, result.getCompanyName());
        assertEquals(DEFAULT_TOTAL_ITEMS, result.getAmountOfItems());
        assertEquals(expectedAmountOfExecutedItems, result.getAmountOfExecutedItems());
        assertEquals(DEFAULT_ITEM_PRICE, result.getPricePerItem());
        assertEquals(TransactionState.STARTED, result.getState());
        assertEquals(TransactionType.SELL, result.getType());
    }

    @Test
    public void testOnBuyTransactionCancelledEventCancelsTransactionView() {
        testSubject.on(new BuyTransactionCancelledEvent(transactionId, DEFAULT_TOTAL_ITEMS, DEFAULT_TOTAL_ITEMS));

        verify(transactionViewRepository).save(transactionViewCaptor.capture());

        TransactionView result = transactionViewCaptor.getValue();
        assertEquals(TransactionState.CANCELLED, result.getState());
    }

    @Test
    public void testOnSellTransactionCancelledEventCancelsTransactionView() {
        testSubject.on(new SellTransactionCancelledEvent(transactionId, DEFAULT_TOTAL_ITEMS, DEFAULT_TOTAL_ITEMS));

        verify(transactionViewRepository).save(transactionViewCaptor.capture());

        TransactionView result = transactionViewCaptor.getValue();
        assertEquals(TransactionState.CANCELLED, result.getState());
    }

    @Test
    public void testOnBuyTransactionConfirmedEventConfirmsTransactionView() {
        testSubject.on(new BuyTransactionConfirmedEvent(transactionId));

        verify(transactionViewRepository).save(transactionViewCaptor.capture());

        TransactionView result = transactionViewCaptor.getValue();
        assertEquals(TransactionState.CONFIRMED, result.getState());
    }

    @Test
    public void testOnSellTransactionConfirmedEventConfirmsTransactionView() {
        testSubject.on(new SellTransactionConfirmedEvent(transactionId));

        verify(transactionViewRepository).save(transactionViewCaptor.capture());

        TransactionView result = transactionViewCaptor.getValue();
        assertEquals(TransactionState.CONFIRMED, result.getState());
    }

    @Test
    public void testOnBuyTransactionExecutedEventAdjustsExecutedItemsAndPrice() {
        long testAmountOfItems = 2L;
        long testItemPrice = 50L;
        long expectedPricePerItem = (testAmountOfItems * testItemPrice) / DEFAULT_TOTAL_ITEMS;

        testSubject.on(new BuyTransactionExecutedEvent(transactionId, testAmountOfItems, testItemPrice));

        verify(transactionViewRepository).save(transactionViewCaptor.capture());

        TransactionView result = transactionViewCaptor.getValue();
        assertEquals(TransactionState.EXECUTED, result.getState());
        assertEquals(DEFAULT_TOTAL_ITEMS, result.getAmountOfExecutedItems());
        assertEquals(expectedPricePerItem, result.getPricePerItem());
    }

    @Test
    public void testOnSellTransactionExecutedEventAdjustsExecutedItemsAndPrice() {
        long testAmountOfItems = 2L;
        long testItemPrice = 50L;
        long expectedPricePerItem = (testAmountOfItems * testItemPrice) / DEFAULT_TOTAL_ITEMS;

        testSubject.on(new SellTransactionExecutedEvent(transactionId, testAmountOfItems, testItemPrice));

        verify(transactionViewRepository).save(transactionViewCaptor.capture());

        TransactionView result = transactionViewCaptor.getValue();
        assertEquals(TransactionState.EXECUTED, result.getState());
        assertEquals(DEFAULT_TOTAL_ITEMS, result.getAmountOfExecutedItems());
        assertEquals(expectedPricePerItem, result.getPricePerItem());
    }

    @Test
    public void testOnBuyTransactionPartiallyExecutedEvent() {
        long testAmountExecutedItems = 3L;
        long testTotalExecutedItems = 4L;
        long testItemPrice = 50L;
        BuyTransactionPartiallyExecutedEvent testEvent = new BuyTransactionPartiallyExecutedEvent(
                transactionId, testAmountExecutedItems, testTotalExecutedItems, testItemPrice
        );

        long expectedPricePerItem = (testAmountExecutedItems * testItemPrice) / testTotalExecutedItems;

        testSubject.on(testEvent);

        verify(transactionViewRepository).save(transactionViewCaptor.capture());

        TransactionView result = transactionViewCaptor.getValue();
        assertEquals(TransactionState.PARTIALLY_EXECUTED, result.getState());
        assertEquals(testTotalExecutedItems, result.getAmountOfExecutedItems());
        assertEquals(expectedPricePerItem, result.getPricePerItem());
    }

    @Test
    public void testOnSellTransactionPartiallyExecutedEvent() {
        long testAmountExecutedItems = 3L;
        long testTotalExecutedItems = 4L;
        long testItemPrice = 50L;
        SellTransactionPartiallyExecutedEvent testEvent = new SellTransactionPartiallyExecutedEvent(
                transactionId, testAmountExecutedItems, testTotalExecutedItems, testItemPrice
        );

        long expectedPricePerItem = (testAmountExecutedItems * testItemPrice) / testTotalExecutedItems;

        testSubject.on(testEvent);

        verify(transactionViewRepository).save(transactionViewCaptor.capture());

        TransactionView result = transactionViewCaptor.getValue();
        assertEquals(TransactionState.PARTIALLY_EXECUTED, result.getState());
        assertEquals(testTotalExecutedItems, result.getAmountOfExecutedItems());
        assertEquals(expectedPricePerItem, result.getPricePerItem());
    }

    @Test
    public void testFindTransactionByIdReturnsATransactionView() {
        TransactionView testView = new TransactionView();
        when(transactionViewRepository.getOne(transactionId.getIdentifier())).thenReturn(testView);

        TransactionView result = testSubject.find(new TransactionByIdQuery(transactionId));

        assertNotNull(result);
        assertEquals(testView, result);
    }

    @Test
    public void testFindTransactionByIdReturnsNullForNonExistentTransactionId() {
        when(transactionViewRepository.getOne(transactionId.getIdentifier())).thenReturn(null);

        assertNull(testSubject.find(new TransactionByIdQuery(transactionId)));
    }

    @Test
    public void testFindTransactionsByPortfolioIdReturnsATransactionView() {
        List<TransactionView> testViews = new ArrayList<>();
        testViews.add(new TransactionView());
        when(transactionViewRepository.findByPortfolioId(portfolioId.getIdentifier())).thenReturn(testViews);

        List<TransactionView> results = testSubject.find(new TransactionsByPortfolioIdQuery(portfolioId));

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(testViews, results);
    }

    @Test
    public void testFindTransactionsByIdReturnsAnEmptyListForNonExistentTransactionId() {
        when(transactionViewRepository.findByPortfolioId(portfolioId.getIdentifier())).thenReturn(null);

        assertTrue(testSubject.find(new TransactionsByPortfolioIdQuery(portfolioId)).isEmpty());
    }
}
