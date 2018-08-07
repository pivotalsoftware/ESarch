package io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock

import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioCommand
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId
import javax.validation.constraints.Min

data class AddItemsToPortfolioCommand(
        override val portfolioId: PortfolioId,
        val orderBookId: OrderBookId,
        @Min(0) val amountOfItemsToAdd: Long
) : PortfolioCommand(portfolioId)

data class CancelItemReservationForPortfolioCommand(
        override val portfolioId: PortfolioId,
        val orderBookId: OrderBookId,
        val transactionId: TransactionId,
        val amountOfItemsToCancel: Long
) : PortfolioCommand(portfolioId)

data class ConfirmItemReservationForPortfolioCommand(
        override val portfolioId: PortfolioId,
        val orderBookId: OrderBookId,
        val transactionId: TransactionId,
        val amountOfItemsToConfirm: Long
) : PortfolioCommand(portfolioId)

data class ReserveItemsCommand(
        override val portfolioId: PortfolioId,
        val orderBookId: OrderBookId,
        val transactionId: TransactionId,
        val amountOfItemsToReserve: Long
) : PortfolioCommand(portfolioId)
