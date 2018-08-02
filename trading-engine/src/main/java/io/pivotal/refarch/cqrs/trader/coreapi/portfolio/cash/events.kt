package io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash

import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioEvent
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId

data class CashDepositedEvent(
        override val portfolioId: PortfolioId,
        val moneyAddedInCents: Long
) : PortfolioEvent(portfolioId)

data class CashReservationCancelledEvent(
        override val portfolioId: PortfolioId,
        val transactionId: TransactionId,
        val amountOfMoneyToCancel: Long
) : PortfolioEvent(portfolioId)

data class CashReservationConfirmedEvent(
        override val portfolioId: PortfolioId,
        val transactionId: TransactionId,
        val amountOfMoneyConfirmedInCents: Long
) : PortfolioEvent(portfolioId)

data class CashReservationRejectedEvent(
        override val portfolioId: PortfolioId,
        val transactionId: TransactionId,
        val amountToPayInCents: Long
) : PortfolioEvent(portfolioId)

data class CashReservedEvent(
        override val portfolioId: PortfolioId,
        val transactionId: TransactionId,
        val amountToReserve: Long
) : PortfolioEvent(portfolioId)

data class CashWithdrawnEvent(
        override val portfolioId: PortfolioId,
        val amountPaidInCents: Long
) : PortfolioEvent(portfolioId)
