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

package io.pivotal.refarch.cqrs.trader.query.transaction;

import io.pivotal.refarch.cqrs.trader.coreapi.orders.TransactionType;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderBookView;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.AbstractTransactionExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.AbstractTransactionPartiallyExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.AbstractTransactionStartedEvent;
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
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionView;
import io.pivotal.refarch.cqrs.trader.query.orderbook.OrderBookViewRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

@Service
@ProcessingGroup("queryModel")
public class TransactionEventHandler {

    private final OrderBookViewRepository orderBookViewRepository;
    private final TransactionViewRepository transactionViewRepository;

    public TransactionEventHandler(OrderBookViewRepository orderBookViewRepository,
                                   TransactionViewRepository transactionViewRepository) {
        this.orderBookViewRepository = orderBookViewRepository;
        this.transactionViewRepository = transactionViewRepository;
    }

    @EventHandler
    public void on(BuyTransactionStartedEvent event) {
        startTransaction(event, TransactionType.BUY);
    }

    @EventHandler
    public void on(SellTransactionStartedEvent event) {
        startTransaction(event, TransactionType.SELL);
    }

    private void startTransaction(AbstractTransactionStartedEvent event, TransactionType type) {
        OrderBookView orderBookView = orderBookViewRepository.getOne(event.getOrderBookId().getIdentifier());

        TransactionView entry = new TransactionView();
        entry.setAmountOfExecutedItems(0);
        entry.setAmountOfItems((int) event.getTotalItems());
        entry.setPricePerItem(event.getPricePerItem());
        entry.setIdentifier(event.getTransactionId().getIdentifier());
        entry.setOrderBookId(event.getOrderBookId().getIdentifier());
        entry.setPortfolioId(event.getPortfolioId().getIdentifier());
        entry.setState(TransactionState.STARTED);
        entry.setType(type);
        entry.setCompanyName(orderBookView.getCompanyName());

        transactionViewRepository.save(entry);
    }

    @EventHandler
    public void on(BuyTransactionCancelledEvent event) {
        changeStateOfTransaction(event.getTransactionId().getIdentifier(), TransactionState.CANCELLED);
    }

    @EventHandler
    public void on(SellTransactionCancelledEvent event) {
        changeStateOfTransaction(event.getTransactionId().getIdentifier(), TransactionState.CANCELLED);
    }

    @EventHandler
    public void on(BuyTransactionConfirmedEvent event) {
        changeStateOfTransaction(event.getTransactionId().getIdentifier(), TransactionState.CONFIRMED);
    }

    @EventHandler
    public void on(SellTransactionConfirmedEvent event) {
        changeStateOfTransaction(event.getTransactionId().getIdentifier(), TransactionState.CONFIRMED);
    }

    private void changeStateOfTransaction(String identifier, TransactionState newState) {
        TransactionView transactionView = transactionViewRepository.getOne(identifier);

        transactionView.setState(newState);

        transactionViewRepository.save(transactionView);
    }

    @EventHandler
    public void on(BuyTransactionExecutedEvent event) {
        executeTransaction(event);
    }

    @EventHandler
    public void on(SellTransactionExecutedEvent event) {
        executeTransaction(event);
    }

    private void executeTransaction(AbstractTransactionExecutedEvent event) {
        TransactionView transactionView = transactionViewRepository.getOne(event.getTransactionId().getIdentifier());

        long value = transactionView.getAmountOfExecutedItems() * transactionView.getPricePerItem();
        long additionalValue = event.getAmountOfItems() * event.getItemPrice();
        long newPrice = (value + additionalValue) / transactionView.getAmountOfItems();

        transactionView.setState(TransactionState.EXECUTED);
        transactionView.setAmountOfExecutedItems(transactionView.getAmountOfItems());
        transactionView.setPricePerItem(newPrice);

        transactionViewRepository.save(transactionView);
    }

    @EventHandler
    public void on(BuyTransactionPartiallyExecutedEvent event) {
        partiallyExecuteTransaction(event);
    }

    @EventHandler
    public void on(SellTransactionPartiallyExecutedEvent event) {
        partiallyExecuteTransaction(event);
    }

    private void partiallyExecuteTransaction(AbstractTransactionPartiallyExecutedEvent event) {
        TransactionView transactionView = transactionViewRepository.getOne(event.getTransactionId().getIdentifier());

        long value = transactionView.getAmountOfExecutedItems() * transactionView.getPricePerItem();
        long additionalValue = event.getAmountOfExecutedItems() * event.getItemPrice();
        long newPrice = (value + additionalValue) / event.getTotalOfExecutedItems();

        transactionView.setState(TransactionState.PARTIALLY_EXECUTED);
        transactionView.setAmountOfExecutedItems(event.getTotalOfExecutedItems());
        transactionView.setPricePerItem(newPrice);

        transactionViewRepository.save(transactionView);
    }
}
