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

import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.CreateBuyOrderCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.TradeExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.BuyTransactionCancelledEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.BuyTransactionConfirmedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.BuyTransactionExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.BuyTransactionPartiallyExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.BuyTransactionStartedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.ConfirmTransactionCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.ExecutedTransactionCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.CancelCashReservationCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.CashReservationRejectedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.CashReservedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.ConfirmCashReservationCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.ReserveCashCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.AddItemsToPortfolioCommand;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.*;

import static org.axonframework.test.matchers.Matchers.exactSequenceOf;
import static org.axonframework.test.matchers.Matchers.matches;

public class BuyTradeManagerSagaTest {

    private static final long TOTAL_ITEMS = 100;
    private static final long PRICE_PER_ITEM = 10;
    private final TransactionId transactionId = new TransactionId();
    private final OrderBookId orderBookId = new OrderBookId();
    private final PortfolioId portfolioId = new PortfolioId();
    private final OrderId sellOrderId = new OrderId();
    private final OrderId buyOrderId = new OrderId();
    private final TransactionId sellTransactionId = new TransactionId();
    private final BuyTransactionStartedEvent buyTransactionStartedEvent =
            new BuyTransactionStartedEvent(transactionId, orderBookId, portfolioId, TOTAL_ITEMS, PRICE_PER_ITEM);
    private SagaTestFixture<BuyTradeManagerSaga> fixture;

    @Before
    public void setUp() {
        fixture = new SagaTestFixture<>(BuyTradeManagerSaga.class);
    }

    @Test
    public void testThatSagaStartReservesCashOnBuyTransactionStartedEvent() {
        ReserveCashCommand expectedCommand =
                new ReserveCashCommand(portfolioId, transactionId, TOTAL_ITEMS * PRICE_PER_ITEM);

        fixture.givenAggregate(transactionId.toString())
               .published()
               .whenAggregate(transactionId.toString())
               .publishes(buyTransactionStartedEvent)
               .expectActiveSagas(1)
               .expectDispatchedCommands(expectedCommand);
    }

    @Test
    public void testTransactionIsConfirmedOnCashReservedEvent() {
        fixture.givenAggregate(transactionId.toString())
               .published(buyTransactionStartedEvent)
               .whenAggregate(portfolioId.toString())
               .publishes(new CashReservedEvent(portfolioId, transactionId, TOTAL_ITEMS * PRICE_PER_ITEM))
               .expectActiveSagas(1)
               .expectDispatchedCommands(new ConfirmTransactionCommand(transactionId));
    }

    @Test
    public void testSagaIsClosedOnCashReservationRejectedEvent() {
        fixture.givenAggregate(transactionId.toString())
               .published(buyTransactionStartedEvent)
               .whenAggregate(portfolioId.toString())
               .publishes(new CashReservationRejectedEvent(portfolioId, transactionId, TOTAL_ITEMS * PRICE_PER_ITEM))
               .expectActiveSagas(0);
    }

    @Test
    public void testBuyOrderCreatedOnBuyTransactionConfirmedEvent() {
        fixture.givenAggregate(transactionId.toString())
               .published(buyTransactionStartedEvent)
               .andThenAggregate(portfolioId.toString())
               .published(new CashReservedEvent(portfolioId, transactionId, TOTAL_ITEMS * PRICE_PER_ITEM))
               .whenAggregate(transactionId.toString())
               .publishes(new BuyTransactionConfirmedEvent(transactionId))
               .expectActiveSagas(1)
               .expectDispatchedCommandsMatching(exactSequenceOf(matches(
                       command -> matchCreateBuyOrderCommand(command,
                                                             orderBookId,
                                                             portfolioId,
                                                             TOTAL_ITEMS,
                                                             PRICE_PER_ITEM)
               )));
    }

    @SuppressWarnings({"unchecked", "SameParameterValue"})
    private boolean matchCreateBuyOrderCommand(Object resultCommand,
                                               OrderBookId expectedOrderBookId,
                                               PortfolioId expectedPortfolioId,
                                               long expectedTradeCount,
                                               long expectedItemPrice) {
        if (!(resultCommand instanceof CommandMessage)) {
            return false;
        }
        CommandMessage<CreateBuyOrderCommand> commandMessage = (CommandMessage<CreateBuyOrderCommand>) resultCommand;
        CreateBuyOrderCommand commandPayload = commandMessage.getPayload();

        return expectedOrderBookId.equals(commandPayload.getOrderBookId())
                && expectedPortfolioId.equals(commandPayload.getPortfolioId())
                && expectedTradeCount == commandPayload.getTradeCount()
                && expectedItemPrice == commandPayload.getItemPrice();
    }

    @Test
    public void testThatCashReservationIsCancelledOnBuyTransactionCancelledEvent() {
        CancelCashReservationCommand expectedCommand =
                new CancelCashReservationCommand(portfolioId, transactionId, TOTAL_ITEMS * PRICE_PER_ITEM);

        fixture.givenAggregate(transactionId.toString())
               .published(buyTransactionStartedEvent)
               .whenAggregate(transactionId.toString())
               .publishes(new BuyTransactionCancelledEvent(transactionId, TOTAL_ITEMS, 0))
               .expectActiveSagas(1)
               .expectDispatchedCommands(expectedCommand);
    }

    @Test
    public void testExecutedTransactionIsPublishedOnTradeExecutedEvent() {
        TradeExecutedEvent givenEvent = new TradeExecutedEvent(
                orderBookId, TOTAL_ITEMS, 99, buyOrderId, sellOrderId, transactionId, sellTransactionId
        );

        fixture.givenAggregate(transactionId.toString())
               .published(buyTransactionStartedEvent)
               .andThenAggregate(portfolioId.toString())
               .published(new CashReservedEvent(portfolioId, transactionId, TOTAL_ITEMS * PRICE_PER_ITEM))
               .andThenAggregate(transactionId.toString())
               .published(new BuyTransactionConfirmedEvent(transactionId))
               .whenAggregate(orderBookId.toString())
               .publishes(givenEvent)
               .expectActiveSagas(1)
               .expectDispatchedCommands(new ExecutedTransactionCommand(transactionId, TOTAL_ITEMS, 99));
    }

    @Test
    public void testThatCashAndItemsAreConfirmedAndSagaIsClosedOnBuyTransactionExecutedEvent() {
        TradeExecutedEvent testTradeExecutedEvent = new TradeExecutedEvent(
                orderBookId, TOTAL_ITEMS, 99, buyOrderId, sellOrderId, transactionId, sellTransactionId
        );

        fixture.givenAggregate(transactionId.toString())
               .published(buyTransactionStartedEvent)
               .andThenAggregate(portfolioId.toString())
               .published(new CashReservedEvent(portfolioId, transactionId, TOTAL_ITEMS * PRICE_PER_ITEM))
               .andThenAggregate(transactionId.toString())
               .published(new BuyTransactionConfirmedEvent(transactionId))
               .andThenAggregate(orderBookId.toString())
               .published(testTradeExecutedEvent)
               .whenAggregate(transactionId.toString())
               .publishes(new BuyTransactionExecutedEvent(transactionId, TOTAL_ITEMS, 99))
               .expectActiveSagas(0)
               .expectDispatchedCommands(
                       new ConfirmCashReservationCommand(portfolioId, transactionId, TOTAL_ITEMS * 99),
                       new AddItemsToPortfolioCommand(portfolioId, orderBookId, TOTAL_ITEMS)
               );
    }

    @Test
    public void testThatCashAndItemsAreConfirmedAndSagaIsClosedOnBuyTransactionExecutedWithLowerExecutedPriceThanBidPrice() {
        TradeExecutedEvent testTradeExecutedEvent = new TradeExecutedEvent(
                orderBookId, TOTAL_ITEMS, 5, buyOrderId, sellOrderId, transactionId, sellTransactionId
        );

        fixture.givenAggregate(transactionId.toString())
               .published(buyTransactionStartedEvent)
               .andThenAggregate(portfolioId.toString())
               .published(new CashReservedEvent(portfolioId, transactionId, TOTAL_ITEMS * PRICE_PER_ITEM))
               .andThenAggregate(transactionId.toString())
               .published(new BuyTransactionConfirmedEvent(transactionId))
               .andThenAggregate(orderBookId.toString())
               .published(testTradeExecutedEvent)
               .whenAggregate(transactionId.toString())
               .publishes(new BuyTransactionExecutedEvent(transactionId, TOTAL_ITEMS, 5))
               .expectActiveSagas(0)
               .expectDispatchedCommands(
                       new CancelCashReservationCommand(portfolioId, transactionId, TOTAL_ITEMS * 5),
                       new ConfirmCashReservationCommand(portfolioId, transactionId, TOTAL_ITEMS * 5),
                       new AddItemsToPortfolioCommand(portfolioId, orderBookId, TOTAL_ITEMS)
               );
    }

    @Test
    public void testThatCashAndItemsAreConfirmedOnBuyTransactionPartiallyExecutedEvent() {
        TradeExecutedEvent testTradeExecutedEvent = new TradeExecutedEvent(
                orderBookId, 50, 99, buyOrderId, sellOrderId, transactionId, sellTransactionId
        );

        fixture.givenAggregate(transactionId.toString())
               .published(buyTransactionStartedEvent)
               .andThenAggregate(portfolioId.toString())
               .published(new CashReservedEvent(portfolioId, transactionId, TOTAL_ITEMS * PRICE_PER_ITEM))
               .andThenAggregate(transactionId.toString())
               .published(new BuyTransactionConfirmedEvent(transactionId))
               .andThenAggregate(orderBookId.toString())
               .published(testTradeExecutedEvent)
               .whenAggregate(transactionId.toString())
               .publishes(new BuyTransactionPartiallyExecutedEvent(transactionId, 50, 50, 99))
               .expectActiveSagas(1)
               .expectDispatchedCommands(
                       new ConfirmCashReservationCommand(portfolioId, transactionId, 50 * 99),
                       new AddItemsToPortfolioCommand(portfolioId, orderBookId, 50)
               );
    }

    @Test
    public void testThatCashAndItemsAreConfirmedOnBuyTransactionPartiallyExecutedWithLowerExecutedPriceThanBidPrice() {
        TradeExecutedEvent testTradeExecutedEvent = new TradeExecutedEvent(
                orderBookId, 50, 5, buyOrderId, sellOrderId, transactionId, sellTransactionId
        );

        fixture.givenAggregate(transactionId.toString())
               .published(buyTransactionStartedEvent)
               .andThenAggregate(portfolioId.toString())
               .published(new CashReservedEvent(portfolioId, transactionId, TOTAL_ITEMS * PRICE_PER_ITEM))
               .andThenAggregate(transactionId.toString())
               .published(new BuyTransactionConfirmedEvent(transactionId))
               .andThenAggregate(orderBookId.toString())
               .published(testTradeExecutedEvent)
               .whenAggregate(transactionId.toString())
               .publishes(new BuyTransactionPartiallyExecutedEvent(transactionId, 50, 50, 5))
               .expectActiveSagas(1)
               .expectDispatchedCommands(
                       new CancelCashReservationCommand(portfolioId, transactionId, 250),
                       new ConfirmCashReservationCommand(portfolioId, transactionId, 50 * 5),
                       new AddItemsToPortfolioCommand(portfolioId, orderBookId, 50)
               );
    }
}
