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

package io.pivotal.refarch.cqrs.trader.app.query.orderbook;

import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyCreatedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.company.OrderBookAddedToCompanyEvent;
import io.pivotal.refarch.cqrs.trader.app.query.company.CompanyEventHandler;
import io.pivotal.refarch.cqrs.trader.app.query.company.CompanyView;
import io.pivotal.refarch.cqrs.trader.app.query.company.CompanyViewRepository;
import io.pivotal.refarch.cqrs.trader.app.query.orders.trades.OrderBookView;
import io.pivotal.refarch.cqrs.trader.app.query.orders.transaction.TradeExecutedView;
import io.pivotal.refarch.cqrs.trader.app.query.orders.transaction.TradeExecutedQueryRepository;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.BuyOrderPlacedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.SellOrderPlacedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.TradeExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author Jettro Coenradie
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
public class OrderBookEventHandlerIntegrationTest {

    OrderId orderId = new OrderId();
    PortfolioId portfolioId = new PortfolioId();
    TransactionId transactionId = new TransactionId();
    OrderBookId orderBookId = new OrderBookId();
    CompanyId companyId = new CompanyId();
    private OrderBookEventHandler orderBookEventHandler;

    @Autowired
    private OrderBookViewRepository orderBookRepository;
    @Autowired
    private TradeExecutedQueryRepository tradeExecutedRepository;
    @Autowired
    private CompanyViewRepository companyRepository;

    @Before
    public void setUp() {
        orderBookRepository.deleteAll();
        companyRepository.deleteAll();
        tradeExecutedRepository.deleteAll();

        CompanyEventHandler companyEventHandler = new CompanyEventHandler(companyRepository);
        companyEventHandler.on(new CompanyCreatedEvent(companyId, "Test Company", 100, 100));

        orderBookEventHandler = new OrderBookEventHandler(orderBookRepository,
                                                          companyRepository,
                                                          tradeExecutedRepository);
    }

    @Test
    public void testHandleOrderBookCreatedEvent() {
        OrderBookAddedToCompanyEvent event = new OrderBookAddedToCompanyEvent(companyId, orderBookId);

        orderBookEventHandler.on(event);
        Iterable<OrderBookView> all = orderBookRepository.findAll();
        OrderBookView orderBookView = all.iterator().next();
        assertNotNull("The first item of the iterator for orderbooks should not be null", orderBookView);
        assertEquals("Test Company", orderBookView.getCompanyName());
    }

    @Test
    public void testHandleBuyOrderPlaced() {
        CompanyView company = createCompany();
        OrderBookView orderBook = createOrderBook(company);

        BuyOrderPlacedEvent event = new BuyOrderPlacedEvent(orderBookId, orderId, transactionId, 300, 100, portfolioId);

        orderBookEventHandler.on(event);
        Iterable<OrderBookView> all = orderBookRepository.findAll();
        OrderBookView orderBookView = all.iterator().next();
        assertNotNull("The first item of the iterator for orderbooks should not be null", orderBookView);
        assertEquals("Test Company", orderBookView.getCompanyName());
        assertEquals(1, orderBookView.buyOrders().size());
        assertEquals(300, orderBookView.buyOrders().get(0).getTradeCount());
    }

    @Test
    public void testHandleSellOrderPlaced() {
        CompanyView company = createCompany();
        OrderBookView orderBook = createOrderBook(company);

        OrderBookId orderBookId = new OrderBookId(orderBook.getIdentifier());
        SellOrderPlacedEvent event = new SellOrderPlacedEvent(orderBookId,
                                                              orderId,
                                                              transactionId,
                                                              300,
                                                              100,
                                                              portfolioId);

        orderBookEventHandler.on(event);
        Iterable<OrderBookView> all = orderBookRepository.findAll();
        OrderBookView orderBookView = all.iterator().next();
        assertNotNull("The first item of the iterator for orderbooks should not be null", orderBookView);
        assertEquals("Test Company", orderBookView.getCompanyName());
        assertEquals(1, orderBookView.sellOrders().size());
        assertEquals(300, orderBookView.sellOrders().get(0).getTradeCount());
    }

    @Test
    public void testHandleTradeExecuted() {
        CompanyView company = createCompany();
        OrderBookView orderBook = createOrderBook(company);

        OrderId sellOrderId = new OrderId();
        TransactionId sellTransactionId = new TransactionId();
        SellOrderPlacedEvent sellOrderPlacedEvent = new SellOrderPlacedEvent(orderBookId,
                                                                             sellOrderId,
                                                                             sellTransactionId,
                                                                             400,
                                                                             100,
                                                                             portfolioId);

        orderBookEventHandler.on(sellOrderPlacedEvent);

        OrderId buyOrderId = new OrderId();
        TransactionId buyTransactionId = new TransactionId();
        BuyOrderPlacedEvent buyOrderPlacedEvent = new BuyOrderPlacedEvent(orderBookId
                , buyOrderId,
                                                                          buyTransactionId,
                                                                          300,
                                                                          150,
                                                                          portfolioId);

        orderBookEventHandler.on(buyOrderPlacedEvent);

        Iterable<OrderBookView> all = orderBookRepository.findAll();
        OrderBookView orderBookView = all.iterator().next();
        assertNotNull("The first item of the iterator for orderbooks should not be null", orderBookView);
        assertEquals("Test Company", orderBookView.getCompanyName());
        assertEquals(1, orderBookView.sellOrders().size());
        assertEquals(1, orderBookView.buyOrders().size());


        TradeExecutedEvent event = new TradeExecutedEvent(orderBookId,
                                                          300,
                                                          125,
                                                          buyOrderId,
                                                          sellOrderId,
                                                          buyTransactionId,
                                                          sellTransactionId);
        orderBookEventHandler.on(event);

        Iterable<TradeExecutedView> tradeExecutedEntries = tradeExecutedRepository.findAll();
        assertTrue(tradeExecutedEntries.iterator().hasNext());
        TradeExecutedView tradeExecutedView = tradeExecutedEntries.iterator().next();
        assertEquals("Test Company", tradeExecutedView.getCompanyName());
        assertEquals(300, tradeExecutedView.getTradeCount());
        assertEquals(125, tradeExecutedView.getTradePrice());

        all = orderBookRepository.findAll();
        orderBookView = all.iterator().next();
        assertNotNull("The first item of the iterator for orderbooks should not be null", orderBookView);
        assertEquals("Test Company", orderBookView.getCompanyName());
        assertEquals(1, orderBookView.sellOrders().size());
        assertEquals(0, orderBookView.buyOrders().size());
    }


    private OrderBookView createOrderBook(CompanyView company) {
        OrderBookView orderBookView = new OrderBookView();
        orderBookView.setIdentifier(orderBookId.getIdentifier());
        orderBookView.setCompanyIdentifier(company.getIdentifier());
        orderBookView.setCompanyName(company.getName());
        orderBookRepository.save(orderBookView);
        return orderBookView;
    }

    private CompanyView createCompany() {
        CompanyId companyId = new CompanyId();
        CompanyView companyView = new CompanyView();
        companyView.setIdentifier(companyId.getIdentifier());
        companyView.setName("Test Company");
        companyView.setAmountOfShares(100000);
        companyView.setTradeStarted(true);
        companyView.setValue(1000);
        companyRepository.save(companyView);
        return companyView;
    }
}
