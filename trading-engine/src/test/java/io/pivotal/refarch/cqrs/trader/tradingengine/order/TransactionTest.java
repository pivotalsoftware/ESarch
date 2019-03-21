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

package io.pivotal.refarch.cqrs.trader.tradingengine.order;

import io.pivotal.refarch.cqrs.trader.tradingengine.order.Transaction;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.BuyTransactionCancelledEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.BuyTransactionConfirmedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.BuyTransactionExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.BuyTransactionPartiallyExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.BuyTransactionStartedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.CancelTransactionCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.ConfirmTransactionCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.ExecutedTransactionCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionCancelledEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionConfirmedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionPartiallyExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionStartedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.StartBuyTransactionCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.StartSellTransactionCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.*;

public class TransactionTest {

    private AggregateTestFixture<Transaction> fixture;

    private TransactionId transactionId = new TransactionId();
    private OrderBookId orderBookId = new OrderBookId();
    private PortfolioId portfolioId = new PortfolioId();

    private BuyTransactionStartedEvent buyTransactionStartedEvent;
    private SellTransactionStartedEvent sellTransactionStartedEvent;

    @Before
    public void setUp() {
        fixture = new AggregateTestFixture<>(Transaction.class);

        buyTransactionStartedEvent = new BuyTransactionStartedEvent(transactionId, orderBookId, portfolioId, 200L, 20L);
        sellTransactionStartedEvent =
                new SellTransactionStartedEvent(transactionId, orderBookId, portfolioId, 200L, 20L);
    }

    @Test
    public void testStartBuyTransaction() {
        fixture.givenNoPriorActivity()
               .when(new StartBuyTransactionCommand(transactionId, orderBookId, portfolioId, 200L, 20L))
               .expectEvents(buyTransactionStartedEvent);
    }

    @Test
    public void testConfirmTransactionOnBuyTransaction() {
        fixture.given(buyTransactionStartedEvent)
               .when(new ConfirmTransactionCommand(transactionId))
               .expectEvents(new BuyTransactionConfirmedEvent(transactionId));
    }

    @Test
    public void testCancelTransactionOnBuyTransaction() {
        fixture.given(buyTransactionStartedEvent)
               .when(new CancelTransactionCommand(transactionId))
               .expectEvents(new BuyTransactionCancelledEvent(transactionId, 200L, 0L));
    }

    @Test
    public void testCancelTransactionOnBuyTransaction_partiallyExecuted() {
        fixture.given(buyTransactionStartedEvent,
                      new BuyTransactionPartiallyExecutedEvent(transactionId, 100L, 100L, 20L))
               .when(new CancelTransactionCommand(transactionId))
               .expectEvents(new BuyTransactionCancelledEvent(transactionId, 200L, 100L));
    }

    @Test
    public void testExecuteTransactionOnBuyTransaction() {
        fixture.given(buyTransactionStartedEvent,
                      new BuyTransactionConfirmedEvent(transactionId))
               .when(new ExecutedTransactionCommand(transactionId, 200L, 20L))
               .expectEvents(new BuyTransactionExecutedEvent(transactionId, 200L, 20L));
    }

    @Test
    public void testExecuteTransactionOnBuyTransaction_partiallyExecuted() {
        fixture.given(buyTransactionStartedEvent,
                      new BuyTransactionConfirmedEvent(transactionId))
               .when(new ExecutedTransactionCommand(transactionId, 50L, 20L))
               .expectEvents(new BuyTransactionPartiallyExecutedEvent(transactionId, 50L, 50L, 20L));
    }

    @Test
    public void testExecuteTransactionOnBuyTransaction_completeAfterPartiallyExecuted() {
        fixture.given(buyTransactionStartedEvent,
                      new BuyTransactionConfirmedEvent(transactionId),
                      new BuyTransactionPartiallyExecutedEvent(transactionId, 50L, 50L, 20L))
               .when(new ExecutedTransactionCommand(transactionId, 150L, 20L))
               .expectEvents(new BuyTransactionExecutedEvent(transactionId, 150, 20L));
    }

    @Test
    public void testStartSellTransaction() {
        fixture.givenNoPriorActivity()
               .when(new StartSellTransactionCommand(transactionId, orderBookId, portfolioId, 200L, 20L))
               .expectEvents(sellTransactionStartedEvent);
    }

    @Test
    public void testConfirmTransactionOnSellTransaction() {
        fixture.given(sellTransactionStartedEvent)
               .when(new ConfirmTransactionCommand(transactionId))
               .expectEvents(new SellTransactionConfirmedEvent(transactionId));
    }

    @Test
    public void testCancelTransactionOnSellTransaction() {
        fixture.given(sellTransactionStartedEvent)
               .when(new CancelTransactionCommand(transactionId))
               .expectEvents(new SellTransactionCancelledEvent(transactionId, 200L, 0L));
    }

    @Test
    public void testCancelTransactionOnSellTransaction_partiallyExecuted() {
        fixture.given(sellTransactionStartedEvent,
                      new SellTransactionPartiallyExecutedEvent(transactionId, 100L, 100L, 20L))
               .when(new CancelTransactionCommand(transactionId))
               .expectEvents(new SellTransactionCancelledEvent(transactionId, 200L, 100L));
    }

    @Test
    public void testExecuteTransactionOnSellTransaction() {
        fixture.given(sellTransactionStartedEvent,
                      new SellTransactionConfirmedEvent(transactionId))
               .when(new ExecutedTransactionCommand(transactionId, 200L, 20L))
               .expectEvents(new SellTransactionExecutedEvent(transactionId, 200L, 20L));
    }

    @Test
    public void testExecuteTransactionOnSellTransaction_partiallyExecuted() {
        fixture.given(sellTransactionStartedEvent,
                      new SellTransactionConfirmedEvent(transactionId))
               .when(new ExecutedTransactionCommand(transactionId, 50L, 20L))
               .expectEvents(new SellTransactionPartiallyExecutedEvent(transactionId, 50L, 50L, 20L));
    }

    @Test
    public void testExecuteTransactionOnSellTransaction_completeAfterPartiallyExecuted() {
        fixture.given(sellTransactionStartedEvent,
                      new SellTransactionConfirmedEvent(transactionId),
                      new SellTransactionPartiallyExecutedEvent(transactionId, 50L, 50L, 20L))
               .when(new ExecutedTransactionCommand(transactionId, 150L, 20L))
               .expectEvents(new SellTransactionExecutedEvent(transactionId, 150, 20L));
    }
}
