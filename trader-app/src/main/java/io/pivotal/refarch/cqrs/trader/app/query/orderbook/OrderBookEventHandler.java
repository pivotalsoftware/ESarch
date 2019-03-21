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

import io.pivotal.refarch.cqrs.trader.coreapi.company.OrderBookAddedToCompanyEvent;
import io.pivotal.refarch.cqrs.trader.app.query.company.CompanyView;
import io.pivotal.refarch.cqrs.trader.app.query.company.CompanyViewRepository;
import io.pivotal.refarch.cqrs.trader.app.query.orders.trades.OrderBookView;
import io.pivotal.refarch.cqrs.trader.app.query.orders.trades.OrderView;
import io.pivotal.refarch.cqrs.trader.app.query.orders.transaction.TradeExecutedView;
import io.pivotal.refarch.cqrs.trader.app.query.tradeexecuted.TradeExecutedQueryRepository;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.*;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ProcessingGroup("trading")
public class OrderBookEventHandler {

    private static final String BUY = "Buy";
    private static final String SELL = "Sell";

    private final OrderBookViewRepository orderBookRepository;
    private final CompanyViewRepository companyRepository;
    private final TradeExecutedQueryRepository tradeExecutedRepository;

    public OrderBookEventHandler(OrderBookViewRepository orderBookRepository,
                                 CompanyViewRepository companyRepository,
                                 TradeExecutedQueryRepository tradeExecutedRepository) {
        this.orderBookRepository = orderBookRepository;
        this.companyRepository = companyRepository;
        this.tradeExecutedRepository = tradeExecutedRepository;
    }

    @QueryHandler
    public List<OrderBookView> findOrderViews(OrderBookViewQuery query) {
        return orderBookRepository.findByCompanyIdentifier(query.getCompanyId().getIdentifier());
    }

    @EventHandler
    public void on(OrderBookAddedToCompanyEvent event) {
        CompanyView companyView = companyRepository.getOne(event.getCompanyId().getIdentifier());

        OrderBookView orderBookView = new OrderBookView();
        orderBookView.setCompanyIdentifier(event.getCompanyId().getIdentifier());
        orderBookView.setCompanyName(companyView.getName());
        orderBookView.setIdentifier(event.getOrderBookId().getIdentifier());

        orderBookRepository.save(orderBookView);
    }

    @EventHandler
    public void on(BuyOrderPlacedEvent event) {
        OrderBookView orderBook = orderBookRepository.getOne(event.getOrderBookId().getIdentifier());

        OrderView buyOrder = createPlacedOrder(event, BUY);
        orderBook.buyOrders().add(buyOrder);

        orderBookRepository.save(orderBook);
    }

    @EventHandler
    public void on(SellOrderPlacedEvent event) {
        OrderBookView orderBook = orderBookRepository.getOne(event.getOrderBookId().getIdentifier());

        OrderView sellOrder = createPlacedOrder(event, SELL);
        orderBook.sellOrders().add(sellOrder);

        orderBookRepository.save(orderBook);
    }

    @EventHandler
    public void on(TradeExecutedEvent event) {
        OrderId buyOrderId = event.getBuyOrderId();
        OrderId sellOrderId = event.getSellOrderId();

        OrderBookId orderBookIdentifier = event.getOrderBookId();
        OrderBookView orderBookView = orderBookRepository.getOne(orderBookIdentifier.getIdentifier());

        TradeExecutedView tradeExecutedView = new TradeExecutedView();
        tradeExecutedView.setCompanyName(orderBookView.getCompanyName());
        tradeExecutedView.setOrderBookId(orderBookView.getIdentifier());
        tradeExecutedView.setTradeCount(event.getTradeCount());
        tradeExecutedView.setTradePrice(event.getTradePrice());

        tradeExecutedRepository.save(tradeExecutedView);

        OrderView foundBuyOrder = null;
        for (OrderView order : orderBookView.buyOrders()) {
            if (order.getIdentifier().equals(buyOrderId.getIdentifier())) {
                long itemsRemaining = order.getItemsRemaining();
                order.setItemsRemaining(itemsRemaining - event.getTradeCount());
                foundBuyOrder = order;
                break;
            }
        }
        if (null != foundBuyOrder && foundBuyOrder.getItemsRemaining() == 0) {
            orderBookView.buyOrders().remove(foundBuyOrder);
        }
        OrderView foundSellOrder = null;
        for (OrderView order : orderBookView.sellOrders()) {
            if (order.getIdentifier().equals(sellOrderId.getIdentifier())) {
                long itemsRemaining = order.getItemsRemaining();
                order.setItemsRemaining(itemsRemaining - event.getTradeCount());
                foundSellOrder = order;
                break;
            }
        }
        if (null != foundSellOrder && foundSellOrder.getItemsRemaining() == 0) {
            orderBookView.sellOrders().remove(foundSellOrder);
        }
        orderBookRepository.save(orderBookView);
    }

    private OrderView createPlacedOrder(AbstractOrderPlacedEvent event, String type) {
        OrderView entry = new OrderView();

        entry.setIdentifier(event.getOrderId().getIdentifier());
        entry.setItemsRemaining(event.getTradeCount());
        entry.setTradeCount(event.getTradeCount());
        entry.setUserId(event.getPortfolioId().getIdentifier());
        entry.setType(type);
        entry.setItemPrice(event.getItemPrice());

        return entry;
    }
}
