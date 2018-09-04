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

package io.pivotal.refarch.cqrs.trader.tradingengine.order;

import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.CreateSellOrderCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.TradeExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.ConfirmTransactionCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.ExecutedTransactionCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionCancelledEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionConfirmedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionPartiallyExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.SellTransactionStartedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.DepositCashCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.CancelItemReservationForPortfolioCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ConfirmItemReservationForPortfolioCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemsReservedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.NotEnoughItemsAvailableToReserveInPortfolioEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ReserveItemsCommand;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.*;

import static org.axonframework.test.matchers.Matchers.exactSequenceOf;
import static org.axonframework.test.matchers.Matchers.matches;

public class SellTradeManagerSagaTest {

    private SagaTestFixture<SellTradeManagerSaga> fixture;

    private final TransactionId transactionId = new TransactionId();
    private final OrderBookId orderBookId = new OrderBookId();
    private final PortfolioId portfolioId = new PortfolioId();
    private final OrderId buyOrderId = new OrderId();
    private final OrderId sellOrderId = new OrderId();
    private final TransactionId buyTransactionId = new TransactionId();
    private final SellTransactionStartedEvent sellTransactionStartedEvent =
            new SellTransactionStartedEvent(transactionId, orderBookId, portfolioId, 100, 10);

    @Before
    public void setUp() {
        fixture = new SagaTestFixture<>(SellTradeManagerSaga.class);
    }

    @Test
    public void testThatSagaStartsAndReservesItemsOnSellTransactionStartedEvent() {
        fixture.givenAggregate(transactionId.toString())
               .published()
               .whenAggregate(transactionId.toString())
               .publishes(sellTransactionStartedEvent)
               .expectActiveSagas(1)
               .expectDispatchedCommands(new ReserveItemsCommand(portfolioId, orderBookId, transactionId, 100));
    }

    @Test
    public void testThatTransactionIsConfirmedOnItemsReservedEvent() {
        fixture.givenAggregate(transactionId.toString())
               .published(sellTransactionStartedEvent)
               .whenAggregate(portfolioId.toString())
               .publishes(new ItemsReservedEvent(portfolioId, orderBookId, transactionId, 100))
               .expectActiveSagas(1)
               .expectDispatchedCommands(new ConfirmTransactionCommand(transactionId));
    }

    @Test
    public void testThatSellOrderIsCreatedOnSellTransactionConfirmedEvent() {
        fixture.givenAggregate(transactionId.toString())
               .published(sellTransactionStartedEvent)
               .andThenAggregate(portfolioId.toString())
               .published(new ItemsReservedEvent(portfolioId, orderBookId, transactionId, 100))
               .whenAggregate(transactionId.toString())
               .publishes(new SellTransactionConfirmedEvent(transactionId))
               .expectActiveSagas(1)
               .expectDispatchedCommandsMatching(exactSequenceOf(matches(
                       command -> matchCreateSellOrderCommand(command, orderBookId, portfolioId, 100, 10)
               )));
    }

    @SuppressWarnings({"unchecked", "SameParameterValue"})
    private boolean matchCreateSellOrderCommand(Object resultCommand,
                                                OrderBookId expectedOrderBookId,
                                                PortfolioId expectedPortfolioId,
                                                int expectedTradeCount,
                                                int expectedItemPrice) {
        if (!(resultCommand instanceof CommandMessage)) {
            return false;
        }
        CommandMessage<CreateSellOrderCommand> commandMessage = (CommandMessage<CreateSellOrderCommand>) resultCommand;
        CreateSellOrderCommand commandPayload = commandMessage.getPayload();

        return expectedOrderBookId.equals(commandPayload.getOrderBookId())
                && expectedPortfolioId.equals(commandPayload.getPortfolioId())
                && expectedTradeCount == commandPayload.getTradeCount()
                && expectedItemPrice == commandPayload.getItemPrice();
    }

    @Test
    public void testThatSagaIsClosedOnNotEnoughItemsAvailableToReserveInPortfolioEvent() {
        NotEnoughItemsAvailableToReserveInPortfolioEvent givenEvent =
                new NotEnoughItemsAvailableToReserveInPortfolioEvent(portfolioId, orderBookId, transactionId, 50, 100);

        fixture.givenAggregate(transactionId.toString())
               .published(sellTransactionStartedEvent)
               .whenAggregate(portfolioId.toString())
               .publishes(givenEvent)
               .expectActiveSagas(0)
               .expectNoDispatchedCommands();
    }

    @Test
    public void testThatSagaEndsAndCancelsReservationOnSellTransactionCancelledEvent() {
        CancelItemReservationForPortfolioCommand expectedCommand =
                new CancelItemReservationForPortfolioCommand(portfolioId, orderBookId, transactionId, 50);

        fixture.givenAggregate(transactionId.toString())
               .published(sellTransactionStartedEvent)
               .whenAggregate(transactionId.toString())
               .publishes(new SellTransactionCancelledEvent(transactionId, 50, 0))
               .expectActiveSagas(0)
               .expectDispatchedCommands(expectedCommand);
    }

    @Test
    public void testExecutedTransactionIsPublishedOnTradeExecutedEvent() {
        TradeExecutedEvent testTradeExecutedEvent = new TradeExecutedEvent(
                orderBookId, 100, 102, buyOrderId, sellOrderId, buyTransactionId, transactionId
        );

        fixture.givenAggregate(transactionId.toString())
               .published(new SellTransactionStartedEvent(transactionId, orderBookId, portfolioId, 100, 99))
               .andThenAggregate(portfolioId.toString())
               .published(new ItemsReservedEvent(portfolioId, orderBookId, transactionId, 100))
               .andThenAggregate(transactionId.toString())
               .published(new SellTransactionConfirmedEvent(transactionId))
               .whenAggregate(orderBookId.toString())
               .publishes(testTradeExecutedEvent)
               .expectActiveSagas(1)
               .expectDispatchedCommands(new ExecutedTransactionCommand(transactionId, 100, 102));
    }

    @Test
    public void testThatCashIsDepositedAndItemsAreReservedOnSellTransactionExecutedEvent() {
        TradeExecutedEvent testTradeExecutedEvent = new TradeExecutedEvent(
                orderBookId, 100, 102, buyOrderId, sellOrderId, buyTransactionId, transactionId
        );

        fixture.givenAggregate(transactionId.toString())
               .published(new SellTransactionStartedEvent(transactionId, orderBookId, portfolioId, 100, 99))
               .andThenAggregate(portfolioId.toString())
               .published(new ItemsReservedEvent(portfolioId, orderBookId, transactionId, 100))
               .andThenAggregate(transactionId.toString())
               .published(new SellTransactionConfirmedEvent(transactionId))
               .andThenAggregate(orderBookId.toString())
               .published(testTradeExecutedEvent)
               .whenAggregate(transactionId.toString())
               .publishes(new SellTransactionExecutedEvent(transactionId, 100, 102))
               .expectActiveSagas(0)
               .expectDispatchedCommands(
                       new ConfirmItemReservationForPortfolioCommand(portfolioId, orderBookId, transactionId, 100),
                       new DepositCashCommand(portfolioId, 100 * 102)
               );
    }

    @Test
    public void testThatCashIsDepositedAndItemsAreReservedOnSellTransactionPartiallyExecutedEvent() {
        TradeExecutedEvent testTradeExecutedEvent = new TradeExecutedEvent(
                orderBookId, 100, 102, buyOrderId, sellOrderId, buyTransactionId, transactionId
        );

        fixture.givenAggregate(transactionId.toString())
               .published(new SellTransactionStartedEvent(transactionId, orderBookId, portfolioId, 100, 99))
               .andThenAggregate(portfolioId.toString())
               .published(new ItemsReservedEvent(portfolioId, orderBookId, transactionId, 100))
               .andThenAggregate(transactionId.toString())
               .published(new SellTransactionConfirmedEvent(transactionId))
               .andThenAggregate(orderBookId.toString())
               .published(testTradeExecutedEvent)
               .whenAggregate(transactionId.toString())
               .publishes(new SellTransactionPartiallyExecutedEvent(transactionId, 50, 75, 102))
               .expectActiveSagas(1)
               .expectDispatchedCommands(
                       new ConfirmItemReservationForPortfolioCommand(portfolioId, orderBookId, transactionId, 50),
                       new DepositCashCommand(portfolioId, 50 * 102)
               );
    }
}
