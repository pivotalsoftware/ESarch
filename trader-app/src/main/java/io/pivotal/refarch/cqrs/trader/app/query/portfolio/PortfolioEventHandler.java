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

package io.pivotal.refarch.cqrs.trader.app.query.portfolio;

import io.pivotal.refarch.cqrs.trader.app.query.orderbook.OrderBookViewRepository;
import io.pivotal.refarch.cqrs.trader.app.query.orders.trades.OrderBookView;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioByUserIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioCreatedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.CashDepositedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.CashReservationCancelledEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.CashReservationConfirmedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.CashReservedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.CashWithdrawnEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemReservationCancelledForPortfolioEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemReservationConfirmedForPortfolioEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemsAddedToPortfolioEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock.ItemsReservedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Service
@ProcessingGroup("trading")
public class PortfolioEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final PortfolioViewRepository portfolioViewRepository;
    private final OrderBookViewRepository orderBookViewRepository;

    public PortfolioEventHandler(PortfolioViewRepository portfolioViewRepository,
                                 OrderBookViewRepository orderBookViewRepository) {
        this.portfolioViewRepository = portfolioViewRepository;
        this.orderBookViewRepository = orderBookViewRepository;
    }

    @EventHandler
    public void on(PortfolioCreatedEvent event) {
        PortfolioView portfolioView = new PortfolioView();
        portfolioView.setIdentifier(event.getPortfolioId().getIdentifier());
        portfolioView.setUserId(event.getUserId().getIdentifier());
        portfolioView.setAmountOfMoney(0);
        portfolioView.setReservedAmountOfMoney(0);

        portfolioViewRepository.save(portfolioView);
    }

    @EventHandler
    public void on(ItemsAddedToPortfolioEvent event) {
        OrderBookId orderBookId = event.getOrderBookId();
        logger.debug("Handle ItemsAddedToPortfolioEvent for orderBook with identifier {}", orderBookId);

        ItemEntry itemEntry = createItemEntry(orderBookId.getIdentifier(), event.getAmountOfItemsAdded());

        PortfolioView portfolioView = portfolioViewRepository.getOne(event.getPortfolioId().getIdentifier());
        portfolioView.addItemInPossession(itemEntry);

        portfolioViewRepository.save(portfolioView);
    }

    @EventHandler
    public void on(ItemsReservedEvent event) {
        OrderBookId orderBookId = event.getOrderBookId();
        logger.debug("Handle ItemsReservedEvent for orderBook with identifier {}", orderBookId);

        ItemEntry itemEntry = createItemEntry(orderBookId.getIdentifier(), event.getAmountOfItemsReserved());

        PortfolioView portfolioView = portfolioViewRepository.getOne(event.getPortfolioId().getIdentifier());
        portfolioView.addReservedItem(itemEntry);

        portfolioViewRepository.save(portfolioView);
    }

    @EventHandler
    public void on(ItemReservationConfirmedForPortfolioEvent event) {
        OrderBookId orderBookId = event.getOrderBookId();
        logger.debug("Handle ItemReservationConfirmedForPortfolioEvent for orderBook with identifier {}", orderBookId);

        PortfolioView portfolioView = portfolioViewRepository.getOne(event.getPortfolioId().getIdentifier());

        portfolioView.removeReservedItem(orderBookId.getIdentifier(), event.getAmountOfConfirmedItems());
        portfolioView.removeItemsInPossession(orderBookId.getIdentifier(), event.getAmountOfConfirmedItems());

        portfolioViewRepository.save(portfolioView);
    }

    @EventHandler
    public void on(ItemReservationCancelledForPortfolioEvent event) {
        OrderBookId orderBookId = event.getOrderBookId();
        logger.debug("Handle ItemReservationCancelledForPortfolioEvent for orderBook with identifier {}", orderBookId);

        ItemEntry itemEntry = createItemEntry(orderBookId.getIdentifier(), event.getAmountOfCancelledItems());

        PortfolioView portfolioView = portfolioViewRepository.getOne(event.getPortfolioId().getIdentifier());
        portfolioView.removeReservedItem(orderBookId.getIdentifier(), event.getAmountOfCancelledItems());
        portfolioView.addItemInPossession(itemEntry);

        portfolioViewRepository.save(portfolioView);
    }

    private ItemEntry createItemEntry(String identifier, long amount) {
        OrderBookView orderBookView = orderBookViewRepository.getOne(identifier);

        ItemEntry itemEntry = new ItemEntry();
        itemEntry.setIdentifier(identifier);
        itemEntry.setCompanyId(orderBookView.getCompanyIdentifier());
        itemEntry.setCompanyName(orderBookView.getCompanyName());
        itemEntry.setAmount(amount);

        return itemEntry;
    }

    @EventHandler
    public void on(CashDepositedEvent event) {
        PortfolioView portfolioView = portfolioViewRepository.getOne(event.getPortfolioId().getIdentifier());
        portfolioView.setAmountOfMoney(portfolioView.getAmountOfMoney() + event.getMoneyAddedInCents());

        portfolioViewRepository.save(portfolioView);
    }

    @EventHandler
    public void on(CashWithdrawnEvent event) {
        PortfolioView portfolioView = portfolioViewRepository.getOne(event.getPortfolioId().getIdentifier());

        portfolioView.setAmountOfMoney(portfolioView.getAmountOfMoney() - event.getAmountPaidInCents());

        portfolioViewRepository.save(portfolioView);
    }

    @EventHandler
    public void on(CashReservedEvent event) {
        PortfolioView portfolioView = portfolioViewRepository.getOne(event.getPortfolioId().getIdentifier());

        portfolioView.setReservedAmountOfMoney(portfolioView.getReservedAmountOfMoney() + event.getAmountToReserve());

        portfolioViewRepository.save(portfolioView);
    }

    @EventHandler
    public void on(CashReservationCancelledEvent event) {
        PortfolioView portfolioView = portfolioViewRepository.getOne(event.getPortfolioId().getIdentifier());

        portfolioView.setReservedAmountOfMoney(
                portfolioView.getReservedAmountOfMoney() - event.getAmountOfMoneyToCancel()
        );

        portfolioViewRepository.save(portfolioView);
    }

    @EventHandler
    public void on(CashReservationConfirmedEvent event) {
        PortfolioView portfolioView = portfolioViewRepository.getOne(event.getPortfolioId().getIdentifier());

        long reservedAmountOfMoney = portfolioView.getReservedAmountOfMoney();
        long amountOfMoneyConfirmed = event.getAmountOfMoneyConfirmedInCents();
        if (amountOfMoneyConfirmed < reservedAmountOfMoney) {
            portfolioView.setReservedAmountOfMoney(reservedAmountOfMoney - amountOfMoneyConfirmed);
        } else {
            portfolioView.setReservedAmountOfMoney(0);
        }
        portfolioView.setAmountOfMoney(portfolioView.getAmountOfMoney() - amountOfMoneyConfirmed);

        portfolioViewRepository.save(portfolioView);
    }

    @QueryHandler
    public PortfolioView find(PortfolioByIdQuery query) {
        String portfolioId = query.getPortfolioId().getIdentifier();
        //noinspection ConstantConditions
        return Optional.ofNullable(portfolioViewRepository.getOne(portfolioId))
                       .orElseGet(() -> {
                           logger.warn("Tried to retrieve a Portfolio query model with a non existent portfolio id [{}]",
                                       portfolioId);
                           return null;
                       });
    }

    @QueryHandler
    public PortfolioView find(PortfolioByUserIdQuery query) {
        String userId = query.getUserId().getIdentifier();
        return Optional.ofNullable(portfolioViewRepository.findByUserId(userId))
                       .orElseGet(() -> {
                           logger.warn("Tried to retrieve a Portfolio query model with a non existent user id [{}]",
                                       userId);
                           return null;
                       });
    }
}
