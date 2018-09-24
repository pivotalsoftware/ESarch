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

import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateMember;
import org.axonframework.commandhandling.model.ForwardMatchingInstances;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class OrderBook {

    public static final Comparator<Order> BUY_ORDER_COMPARATOR = Comparator.comparingLong(Order::getItemPrice).reversed()
                                                                           .thenComparing(Order::getCounter);
    public static final Comparator<Order> SELL_ORDER_COMPARATOR = Comparator.comparingLong(Order::getItemPrice)
                                                                            .thenComparing(Order::getCounter);

    @AggregateIdentifier
    private OrderBookId orderBookId;

    @AggregateMember(routingKey = "buyOrderId", eventForwardingMode = ForwardMatchingInstances.class)
    private SortedSet<Order> buyOrders;
    @AggregateMember(routingKey = "sellOrderId", eventForwardingMode = ForwardMatchingInstances.class)
    private SortedSet<Order> sellOrders;
    private long orderCounter;

    @SuppressWarnings("UnusedDeclaration")
    public OrderBook() {
        // Required by Axon Framework
    }

    @CommandHandler
    public OrderBook(CreateOrderBookCommand cmd) {
        apply(new OrderBookCreatedEvent(cmd.getOrderBookId()));
    }

    @CommandHandler
    public void handle(CreateBuyOrderCommand cmd) {
        apply(new BuyOrderPlacedEvent(orderBookId,
                                      cmd.getOrderId(),
                                      cmd.getTransactionId(),
                                      cmd.getTradeCount(),
                                      cmd.getItemPrice(),
                                      cmd.getPortfolioId()));
        executeTrades();
    }

    @CommandHandler
    public void handle(CreateSellOrderCommand cmd) {
        apply(new SellOrderPlacedEvent(orderBookId,
                                       cmd.getOrderId(),
                                       cmd.getTransactionId(),
                                       cmd.getTradeCount(),
                                       cmd.getItemPrice(),
                                       cmd.getPortfolioId()));
        executeTrades();
    }

    private void executeTrades() {
        boolean tradingDone = false;
        while (!tradingDone && !buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order highestBuyer = buyOrders.first();
            Order lowestSeller = sellOrders.first();
            if (highestBuyer.getItemPrice() >= lowestSeller.getItemPrice()) {
                long matchedTradeCount = Math.min(highestBuyer.getItemsRemaining(), lowestSeller.getItemsRemaining());
                long matchedTradePrice = ((highestBuyer.getItemPrice() + lowestSeller.getItemPrice()) / 2);
                apply(new TradeExecutedEvent(orderBookId,
                                             matchedTradeCount,
                                             matchedTradePrice,
                                             highestBuyer.getOrderId(),
                                             lowestSeller.getOrderId(),
                                             highestBuyer.getTransactionId(),
                                             lowestSeller.getTransactionId()));
            } else {
                tradingDone = true;
            }
        }
    }

    @EventSourcingHandler
    protected void on(OrderBookCreatedEvent event) {
        this.orderBookId = event.getOrderBookId();
        buyOrders = new TreeSet<>(BUY_ORDER_COMPARATOR);
        sellOrders = new TreeSet<>(SELL_ORDER_COMPARATOR);
    }

    @EventSourcingHandler
    protected void on(BuyOrderPlacedEvent event) {
        buyOrders.add(new Order(event.getOrderId(),
                                event.getTransactionId(),
                                event.getItemPrice(),
                                event.getTradeCount(),
                                event.getPortfolioId(),
                                orderCounter++));
    }

    @EventSourcingHandler
    protected void on(SellOrderPlacedEvent event) {
        sellOrders.add(new Order(event.getOrderId(),
                                 event.getTransactionId(),
                                 event.getItemPrice(),
                                 event.getTradeCount(),
                                 event.getPortfolioId(),
                                 orderCounter++));
    }

    @EventSourcingHandler
    protected void on(TradeExecutedEvent event) {
        Order highestBuyer = buyOrders.first();
        if (highestBuyer.getItemsRemaining() <= event.getTradeCount()) {
            buyOrders.remove(highestBuyer);
        }

        Order lowestSeller = sellOrders.first();
        if (lowestSeller.getItemsRemaining() <= event.getTradeCount()) {
            sellOrders.remove(lowestSeller);
        }
    }

}
