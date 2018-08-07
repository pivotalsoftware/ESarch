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

package io.pivotal.refarch.cqrs.trader.tradingengine.trade;

import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.TradeExecutedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import org.axonframework.commandhandling.model.EntityId;
import org.axonframework.eventsourcing.EventSourcingHandler;

public class Order {

    private final long itemPrice;
    private final long tradeCount;
    private final PortfolioId portfolioId;
    @EntityId
    private OrderId orderId;
    private TransactionId transactionId;
    private long itemsRemaining;

    public Order(OrderId orderId, TransactionId transactionId, long itemPrice, long tradeCount, PortfolioId portfolioId) {
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.itemPrice = itemPrice;
        this.tradeCount = tradeCount;
        this.itemsRemaining = tradeCount;
        this.portfolioId = portfolioId;
    }

    public long getItemPrice() {
        return itemPrice;
    }

    public long getTradeCount() {
        return tradeCount;
    }

    public PortfolioId getPortfolioId() {
        return portfolioId;
    }

    public long getItemsRemaining() {
        return itemsRemaining;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public TransactionId getTransactionId() {
        return transactionId;
    }

    @EventSourcingHandler
    protected void onTradeExecuted(TradeExecutedEvent event) {
        recordTraded(event.getTradeCount());
    }

    private void recordTraded(long tradeCount) {
        this.itemsRemaining -= tradeCount;
    }
}
