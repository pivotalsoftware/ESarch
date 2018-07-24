package io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock

import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioEvent
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId

data class ItemReservationCancelledForPortfolioEvent(
        override val portfolioId: PortfolioId,
        val orderBookId: OrderBookId,
        val transactionId: TransactionId,
        val amountOfCancelledItems: Long
) : PortfolioEvent(portfolioId)

data class ItemReservationConfirmedForPortfolioEvent(
        override val portfolioId: PortfolioId,
        val orderBookId: OrderBookId,
        val transactionId: TransactionId,
        val amountOfConfirmedItems: Long
) : PortfolioEvent(portfolioId)

data class ItemsAddedToPortfolioEvent(
        override val portfolioId: PortfolioId,
        val orderBookId: OrderBookId,
        val amountOfItemsAdded: Long
) : PortfolioEvent(portfolioId)

data class ItemsReservedEvent(
        override val portfolioId: PortfolioId,
        val orderBookId: OrderBookId,
        val transactionId: TransactionId,
        val amountOfItemsReserved: Long
) : PortfolioEvent(portfolioId)

data class ItemToReserveNotAvailableInPortfolioEvent(
        override val portfolioId: PortfolioId,
        val orderBookId: OrderBookId,
        val transactionId: TransactionId
) : PortfolioEvent(portfolioId)

data class NotEnoughItemsAvailableToReserveInPortfolioEvent(
        override val portfolioId: PortfolioId,
        val orderBookId: OrderBookId,
        val transactionId: TransactionId,
        val availableAmountOfItems: Long,
        val amountOfItemsToReserve: Long
) : PortfolioEvent(portfolioId)
