package io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash

import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioCommand
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId
import javax.validation.constraints.Min

data class CancelCashReservationCommand(
        override val portfolioId: PortfolioId,
        val transactionId: TransactionId,
        val amountOfMoneyToCancel: Long
) : PortfolioCommand(portfolioId)

data class ConfirmCashReservationCommand(
        override val portfolioId: PortfolioId,
        val transactionId: TransactionId,
        val amountOfMoneyToConfirmInCents: Long
) : PortfolioCommand(portfolioId)

data class DepositCashCommand(
        override val portfolioId: PortfolioId,
        @Min(0) val moneyToAddInCents: Long
) : PortfolioCommand(portfolioId)

data class ReserveCashCommand(
        override val portfolioId: PortfolioId,
        val transactionId: TransactionId,
        @Min(0) val amountOfMoneyToReserve: Long
) : PortfolioCommand(portfolioId)

data class WithdrawCashCommand(
        override val portfolioId: PortfolioId,
        @Min(0) val amountToPayInCents: Long
) : PortfolioCommand(portfolioId)
