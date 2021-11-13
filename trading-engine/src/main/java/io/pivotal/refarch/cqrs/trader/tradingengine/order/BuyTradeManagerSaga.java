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
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.CreateBuyOrderCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.TradeExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.*;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.*;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.AddItemsToPortfolioCommand;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class BuyTradeManagerSaga {

    private static final Logger logger = LoggerFactory.getLogger(BuyTradeManagerSaga.class);

    private transient CommandGateway commandGateway;

    private TransactionId transactionId;
    private OrderBookId orderBookId;
    private PortfolioId portfolioId;
    private long totalItems;
    private long pricePerItem;

    @StartSaga
    @SagaEventHandler(associationProperty = "transactionId")
    public void handle(BuyTransactionStartedEvent event) {
        transactionId = event.getTransactionId();
        orderBookId = event.getOrderBookId();
        portfolioId = event.getPortfolioId();
        pricePerItem = event.getPricePerItem();
        totalItems = event.getTotalItems();

        logger.debug(
                "A new buy transaction is started with identifier {}, for portfolio with identifier {} and order book with identifier {}",
                transactionId,
                portfolioId,
                orderBookId
        );
        logger.debug(
                "The new buy transaction with identifier {} is for buying {} items for the price of {}",
                transactionId, totalItems, pricePerItem
        );

        commandGateway.send(new ReserveCashCommand(portfolioId, transactionId, totalItems * pricePerItem));
    }

    @SuppressWarnings("unused")
    @SagaEventHandler(associationProperty = "transactionId")
    public void handle(CashReservedEvent event) {
        logger.debug("Money for transaction with identifier {} is reserved", transactionId);

        commandGateway.send(new ConfirmTransactionCommand(transactionId),
                            new CommandCallback<ConfirmTransactionCommand, Void>() {
                                @Override
                                public void onSuccess(CommandMessage commandMessage, Void result) {
                                    // TODO Do we really need this? TODO #28 discuss this with Allard
                                    logger.debug("Confirm transaction is dispatched successfully!");
                                }

                                @Override
                                public void onFailure(CommandMessage commandMessage, Throwable cause) {
                                    logger.error("********* WOW!!!", cause);
                                }
                            });
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "transactionId")
    public void handle(CashReservationRejectedEvent event) {
        logger.debug(
                "Not enough cash was available to make reservation in transaction {} for portfolio {}. Required: {}",
                transactionId, event.getPortfolioId(), event.getAmountToPayInCents()
        );
    }

    @SuppressWarnings("unused")
    @SagaEventHandler(associationProperty = "transactionId")
    public void handle(BuyTransactionConfirmedEvent event) {
        logger.debug("Buy Transaction {} is approved to make the buy order", transactionId);

        commandGateway.send(new CreateBuyOrderCommand(new OrderId(),
                                                      portfolioId,
                                                      orderBookId,
                                                      transactionId,
                                                      totalItems,
                                                      pricePerItem));
    }

    @SagaEventHandler(associationProperty = "transactionId")
    public void handle(BuyTransactionCancelledEvent event) {
        long amountToCancel = (event.getTotalAmountOfItems() - event.getAmountOfExecutedItems()) * pricePerItem;

        logger.debug(
                "Buy Transaction {} is cancelled, amount of cash reserved to cancel is {}",
                transactionId, amountToCancel
        );

        commandGateway.send(new CancelCashReservationCommand(portfolioId, transactionId, amountToCancel));
    }

    @SagaEventHandler(associationProperty = "buyTransactionId", keyName = "transactionId")
    public void handle(TradeExecutedEvent event) {
        long tradeCount = event.getTradeCount();
        long tradePrice = event.getTradePrice();

        logger.debug(
                "Buy Transaction {} is executed, items for transaction are {} for a price of {}",
                transactionId, tradeCount, tradePrice
        );

        commandGateway.send(new ExecutedTransactionCommand(transactionId, tradeCount, tradePrice));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "transactionId")
    public void handle(BuyTransactionExecutedEvent event) {
        long amountOfItems = event.getAmountOfItems();
        long itemPrice = event.getItemPrice();

        logger.debug(
                "Buy Transaction {} is executed, last amount of executed items is {} for a price of {}",
                transactionId, amountOfItems, itemPrice
        );

        returnDifferenceInBidPriceAndExecutedPrice(pricePerItem, itemPrice, amountOfItems);

        commandGateway.send(new ConfirmCashReservationCommand(portfolioId, transactionId, amountOfItems * itemPrice));
        commandGateway.send(new AddItemsToPortfolioCommand(portfolioId, orderBookId, amountOfItems));
    }

    @SagaEventHandler(associationProperty = "transactionId")
    public void handle(BuyTransactionPartiallyExecutedEvent event) {
        long amountOfExecutedItems = event.getAmountOfExecutedItems();
        long itemPrice = event.getItemPrice();

        logger.debug(
                "Buy Transaction {} is partially executed, amount of executed items is {} for a price of {}",
                transactionId, amountOfExecutedItems, itemPrice
        );

        returnDifferenceInBidPriceAndExecutedPrice(pricePerItem, itemPrice, amountOfExecutedItems);

        commandGateway.send(new ConfirmCashReservationCommand(portfolioId,
                                                              transactionId,
                                                              amountOfExecutedItems * itemPrice));
        commandGateway.send(new AddItemsToPortfolioCommand(portfolioId, orderBookId, amountOfExecutedItems));
    }

    @Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    private void returnDifferenceInBidPriceAndExecutedPrice(long bidPrice, long executedPrice,
                                                            long amountOfExecutedItems) {
        long totalDifferenceInCents = amountOfExecutedItems * bidPrice - amountOfExecutedItems * executedPrice;
        if (totalDifferenceInCents > 0) {
            commandGateway.send(new CancelCashReservationCommand(portfolioId, transactionId, totalDifferenceInCents));
        }
    }
}
