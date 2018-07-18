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

package io.pivotal.refarch.cqrs.trader.query.portfolio;

import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderBookView;
import io.pivotal.refarch.cqrs.trader.query.orderbook.OrderBookViewRepository;
import io.pivotal.refarch.cqrs.trader.query.portfolio.repositories.PortfolioViewRepository;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemReservationCancelledForPortfolioEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemReservationConfirmedForPortfolioEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemsAddedToPortfolioEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemsReservedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

/**
 * We setup this test with a default portfolio and a default orderBook. The portfolio contains the default amount of
 * items in Reservation. This means that all available items are reserved.
 */
public class PortfolioItemEventHandlerTest {

    private static final int DEFAULT_AMOUNT_ITEMS = 100;

    private final PortfolioViewRepository portfolioViewRepository = mock(PortfolioViewRepository.class);
    private final OrderBookViewRepository orderBookViewRepository = mock(OrderBookViewRepository.class);

    private PortfolioItemEventHandler testSubject;

    private final UserId userId = new UserId();
    private final OrderBookId itemId = new OrderBookId();
    private final PortfolioId portfolioId = new PortfolioId();
    private final CompanyId companyId = new CompanyId();
    private final TransactionId transactionId = new TransactionId();

    @Before
    public void setUp() {
        when(orderBookViewRepository.getOne(itemId.toString())).thenReturn(createOrderBookEntry());
        when(portfolioViewRepository.getOne(portfolioId.toString())).thenReturn(createPortfolioEntry());

        testSubject = new PortfolioItemEventHandler(portfolioViewRepository, orderBookViewRepository);
    }

    @Test
    public void testHandleEventAddItems() {
        testSubject.on(new ItemsAddedToPortfolioEvent(portfolioId, itemId, 100));

        verify(portfolioViewRepository).save(argThat(new io.pivotal.refarch.cqrs.trader.query.portfolio.PortfolioEntryMatcher(
                itemId.toString(), 1, 2 * DEFAULT_AMOUNT_ITEMS, 1, DEFAULT_AMOUNT_ITEMS
        )));
    }

    @Test
    public void testHandleEventCancelItemReservation() {
        testSubject.on(new ItemReservationCancelledForPortfolioEvent(portfolioId,
                itemId,
                transactionId,
                DEFAULT_AMOUNT_ITEMS));

        verify(portfolioViewRepository).save(argThat(new io.pivotal.refarch.cqrs.trader.query.portfolio.PortfolioEntryMatcher(
                itemId.toString(), 1, 2 * DEFAULT_AMOUNT_ITEMS, 0, 0
        )));
    }

    /**
     * We are going to confirm 50 of the items in the reservation. Therefore we expect the reservation to become 50
     * less than the default amount of items.
     */
    @Test
    public void testHandleEventConfirmItemReservation() {
        testSubject.on(new ItemReservationConfirmedForPortfolioEvent(portfolioId, itemId, transactionId, 50));

        verify(portfolioViewRepository).save(argThat(new io.pivotal.refarch.cqrs.trader.query.portfolio.PortfolioEntryMatcher(
                itemId.toString(), 1, DEFAULT_AMOUNT_ITEMS - 50, 1, DEFAULT_AMOUNT_ITEMS - 50
        )));
    }

    @Test
    public void testHandleItemReservedEvent() {
        testSubject.on(new ItemsReservedEvent(portfolioId, itemId, transactionId, DEFAULT_AMOUNT_ITEMS));

        verify(portfolioViewRepository).save(argThat(new PortfolioEntryMatcher(
                itemId.toString(), 1, DEFAULT_AMOUNT_ITEMS, 1, 2 * DEFAULT_AMOUNT_ITEMS
        )));
    }

    private PortfolioView createPortfolioEntry() {
        PortfolioView portfolioView = new PortfolioView();
        portfolioView.setIdentifier(portfolioId.toString());
        portfolioView.setUserIdentifier(userId.toString());

        portfolioView.addItemInPossession(createItemEntry(itemId, companyId));
        portfolioView.addReservedItem(createItemEntry(itemId, companyId));
        portfolioView.setReservedAmountOfMoney(1000);
        portfolioView.setAmountOfMoney(10000);
        return portfolioView;
    }

    private OrderBookView createOrderBookEntry() {
        OrderBookView orderBookView = new OrderBookView();
        orderBookView.setIdentifier(itemId.toString());
        orderBookView.setCompanyIdentifier(companyId.toString());
        orderBookView.setCompanyName("Test Company");
        return orderBookView;
    }

    private ItemEntry createItemEntry(OrderBookId itemIdentifier, CompanyId companyIdentifier) {
        ItemEntry itemInPossession = new ItemEntry();
        itemInPossession.setIdentifier(itemIdentifier.toString());
        itemInPossession.setCompanyIdentifier(companyIdentifier.toString());
        itemInPossession.setCompanyName("Test company");
        itemInPossession.setAmount(DEFAULT_AMOUNT_ITEMS);
        return itemInPossession;
    }
}
