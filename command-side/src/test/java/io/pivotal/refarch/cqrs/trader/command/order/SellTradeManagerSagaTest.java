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

package io.pivotal.refarch.cqrs.trader.command.order;

import io.pivotal.refarch.cqrs.trader.command.order.matchers.CancelItemReservationForPortfolioCommandMatcher;
import io.pivotal.refarch.cqrs.trader.command.order.matchers.ConfirmItemReservationForPortfolioCommandMatcher;
import io.pivotal.refarch.cqrs.trader.command.order.matchers.ConfirmTransactionCommandMatcher;
import io.pivotal.refarch.cqrs.trader.command.order.matchers.CreateSellOrderCommandMatcher;
import io.pivotal.refarch.cqrs.trader.command.order.matchers.DepositMoneyToPortfolioCommandMatcher;
import io.pivotal.refarch.cqrs.trader.command.order.matchers.ExecutedTransactionCommandMatcher;
import io.pivotal.refarch.cqrs.trader.command.order.matchers.ReservedItemsCommandMatcher;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.TradeExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionCancelledEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionConfirmedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionPartiallyExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionStartedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemsReservedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.NotEnoughItemsAvailableToReserveInPortfolioEvent;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.*;

import static org.axonframework.test.matchers.Matchers.andNoMore;
import static org.axonframework.test.matchers.Matchers.exactSequenceOf;

public class SellTradeManagerSagaTest {

    private TransactionId transactionIdentifier = new TransactionId();
    private OrderBookId orderbookIdentifier = new OrderBookId();
    private PortfolioId portfolioIdentifier = new PortfolioId();

    private SagaTestFixture<SellTradeManagerSaga> fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new SagaTestFixture<>(SellTradeManagerSaga.class);
    }

    @Test
    public void testHandle_SellTransactionStarted() throws Exception {
        fixture.givenAggregate(transactionIdentifier.toString()).published()
               .whenAggregate(transactionIdentifier.toString()).publishes(new SellTransactionStartedEvent(
                transactionIdentifier,
                orderbookIdentifier,
                portfolioIdentifier,
                100,
                10))
               .expectActiveSagas(1)
               .expectDispatchedCommandsMatching(exactSequenceOf(ReservedItemsCommandMatcher
                                                                         .newInstance(orderbookIdentifier,
                                                                                      portfolioIdentifier,
                                                                                      100)));
    }

    @Test
    public void testHandle_ItemsReserved() throws Exception {
        fixture.givenAggregate(transactionIdentifier.toString()).published(new SellTransactionStartedEvent(
                transactionIdentifier,
                orderbookIdentifier,
                portfolioIdentifier,
                100,
                10))
               .whenAggregate(portfolioIdentifier.toString()).publishes(new ItemsReservedEvent(portfolioIdentifier,
                                                                                               orderbookIdentifier,
                                                                                               transactionIdentifier,
                                                                                               100))
               .expectActiveSagas(1)
               .expectDispatchedCommandsMatching(exactSequenceOf(ConfirmTransactionCommandMatcher.newInstance(
                       transactionIdentifier)));
    }

    @Test
    public void testHandle_TransactionConfirmed() throws Exception {
        fixture.givenAggregate(transactionIdentifier.toString()).published(new SellTransactionStartedEvent(
                transactionIdentifier,
                orderbookIdentifier,
                portfolioIdentifier,
                100,
                10))
               .andThenAggregate(portfolioIdentifier.toString()).published(new ItemsReservedEvent(portfolioIdentifier,
                                                                                                  orderbookIdentifier,
                                                                                                  transactionIdentifier,
                                                                                                  100))
               .whenAggregate(transactionIdentifier.toString()).publishes(new SellTransactionConfirmedEvent(
                transactionIdentifier))
               .expectActiveSagas(1)
               .expectDispatchedCommandsMatching(exactSequenceOf(CreateSellOrderCommandMatcher
                                                                         .newInstance(portfolioIdentifier,
                                                                                      orderbookIdentifier,
                                                                                      100,
                                                                                      10)));
    }


    @Test
    public void testHandle_NotEnoughItemsToReserve() throws Exception {
        fixture.givenAggregate(transactionIdentifier.toString()).published(new SellTransactionStartedEvent(
                transactionIdentifier,
                orderbookIdentifier,
                portfolioIdentifier,
                100,
                10))
               .whenAggregate(portfolioIdentifier.toString())
               .publishes(new NotEnoughItemsAvailableToReserveInPortfolioEvent(
                       portfolioIdentifier,
                       orderbookIdentifier,
                       transactionIdentifier,
                       50,
                       100))
               .expectActiveSagas(0);
    }

    @Test
    public void testHandle_TransactionCancelled() throws Exception {
        fixture.givenAggregate(transactionIdentifier.toString()).published(new SellTransactionStartedEvent(
                transactionIdentifier,
                orderbookIdentifier,
                portfolioIdentifier,
                100,
                10))
               .whenAggregate(transactionIdentifier.toString()).publishes(new SellTransactionCancelledEvent(
                transactionIdentifier,
                50,
                0))
               .expectActiveSagas(0)
               .expectDispatchedCommandsMatching(exactSequenceOf(CancelItemReservationForPortfolioCommandMatcher
                                                                         .newInstance(
                                                                                 orderbookIdentifier,
                                                                                 portfolioIdentifier,
                                                                                 50)));
    }

    @Test
    public void testHandle_TradeExecutedPlaced() throws Exception {
        OrderId buyOrderIdentifier = new OrderId();
        OrderId sellOrderIdentifier = new OrderId();
        TransactionId buyTransactionIdentifier = new TransactionId();
        fixture.givenAggregate(transactionIdentifier.toString()).published(new SellTransactionStartedEvent(
                transactionIdentifier,
                orderbookIdentifier,
                portfolioIdentifier,
                100,
                99))
               .andThenAggregate(portfolioIdentifier.toString()).published(new ItemsReservedEvent(portfolioIdentifier,
                                                                                                  orderbookIdentifier,
                                                                                                  transactionIdentifier,
                                                                                                  100))
               .andThenAggregate(transactionIdentifier.toString()).published(new SellTransactionConfirmedEvent(
                transactionIdentifier))
               .whenAggregate(orderbookIdentifier.toString()).publishes(new TradeExecutedEvent(orderbookIdentifier,
                                                                                               100,
                                                                                               102,
                                                                                               buyOrderIdentifier,
                                                                                               sellOrderIdentifier,
                                                                                               buyTransactionIdentifier,
                                                                                               transactionIdentifier))
               .expectActiveSagas(1)
               .expectDispatchedCommandsMatching(exactSequenceOf(ExecutedTransactionCommandMatcher.newInstance(100,
                                                                                                               102,
                                                                                                               transactionIdentifier),
                                                                 andNoMore()));
    }

    @Test
    public void testHandle_SellTransactionExecuted() throws Exception {
        OrderId buyOrderIdentifier = new OrderId();
        OrderId sellOrderIdentifier = new OrderId();
        TransactionId buyTransactionIdentifier = new TransactionId();
        fixture.givenAggregate(transactionIdentifier.toString()).published(new SellTransactionStartedEvent(
                transactionIdentifier,
                orderbookIdentifier,
                portfolioIdentifier,
                100,
                99))
               .andThenAggregate(portfolioIdentifier.toString()).published(new ItemsReservedEvent(portfolioIdentifier,
                                                                                                  orderbookIdentifier,
                                                                                                  transactionIdentifier,
                                                                                                  100))
               .andThenAggregate(transactionIdentifier.toString()).published(new SellTransactionConfirmedEvent(
                transactionIdentifier))
               .andThenAggregate(orderbookIdentifier.toString()).published(new TradeExecutedEvent(orderbookIdentifier,
                                                                                                  100,
                                                                                                  102,
                                                                                                  buyOrderIdentifier,
                                                                                                  sellOrderIdentifier,
                                                                                                  buyTransactionIdentifier,
                                                                                                  transactionIdentifier))
               .whenAggregate(transactionIdentifier.toString()).publishes(new SellTransactionExecutedEvent(
                transactionIdentifier,
                100,
                102))
               .expectActiveSagas(0)
               .expectDispatchedCommandsMatching(
                       exactSequenceOf(
                               ConfirmItemReservationForPortfolioCommandMatcher.newInstance(orderbookIdentifier,
                                                                                            portfolioIdentifier,
                                                                                            100),
                               DepositMoneyToPortfolioCommandMatcher.newInstance(portfolioIdentifier, 100 * 102)));
    }

    @Test
    public void testHandle_SellTransactionPartiallyExecuted() throws Exception {
        OrderId buyOrderIdentifier = new OrderId();
        OrderId sellOrderIdentifier = new OrderId();
        TransactionId buyTransactionIdentifier = new TransactionId();

        fixture.givenAggregate(transactionIdentifier.toString()).published(new SellTransactionStartedEvent(
                transactionIdentifier,
                orderbookIdentifier,
                portfolioIdentifier,
                100,
                99))
               .andThenAggregate(portfolioIdentifier.toString()).published(new ItemsReservedEvent(portfolioIdentifier,
                                                                                                  orderbookIdentifier,
                                                                                                  transactionIdentifier,
                                                                                                  100))
               .andThenAggregate(transactionIdentifier.toString()).published(new SellTransactionConfirmedEvent(
                transactionIdentifier))
               .andThenAggregate(orderbookIdentifier.toString()).published(new TradeExecutedEvent(orderbookIdentifier,
                                                                                                  100,
                                                                                                  102,
                                                                                                  buyOrderIdentifier,
                                                                                                  sellOrderIdentifier,
                                                                                                  buyTransactionIdentifier,
                                                                                                  transactionIdentifier))
               .whenAggregate(transactionIdentifier.toString()).publishes(new SellTransactionPartiallyExecutedEvent(
                transactionIdentifier,
                50,
                75,
                102))
               .expectActiveSagas(1)
               .expectDispatchedCommandsMatching(
                       exactSequenceOf(
                               ConfirmItemReservationForPortfolioCommandMatcher.newInstance(orderbookIdentifier,
                                                                                            portfolioIdentifier,
                                                                                            50),
                               DepositMoneyToPortfolioCommandMatcher.newInstance(portfolioIdentifier, 50 * 102)));
    }
}
