package io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash

import com.fasterxml.jackson.annotation.JsonProperty
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioCommand
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId
import javax.validation.constraints.Min

data class CancelCashReservationCommand(
        @JsonProperty("portfolioId") override val portfolioId: PortfolioId,
        @JsonProperty("transactionId") val transactionId: TransactionId,
        @JsonProperty("amountOfMoneyToCancel") val amountOfMoneyToCancel: Long
) : PortfolioCommand(portfolioId)

data class ConfirmCashReservationCommand(
        @JsonProperty("portfolioId") override val portfolioId: PortfolioId,
        @JsonProperty("transactionId") val transactionId: TransactionId,
        @JsonProperty("amountOfMoneyToConfirmInCents") val amountOfMoneyToConfirmInCents: Long
) : PortfolioCommand(portfolioId)

data class DepositCashCommand(
        @JsonProperty("portfolioId") override val portfolioId: PortfolioId,
        @JsonProperty("moneyToAddInCents") @Min(0) val moneyToAddInCents: Long
) : PortfolioCommand(portfolioId)

data class ReserveCashCommand(
        @JsonProperty("portfolioId") override val portfolioId: PortfolioId,
        @JsonProperty("transactionId") val transactionId: TransactionId,
        @JsonProperty("amountOfMoneyToReserve") @Min(0) val amountOfMoneyToReserve: Long
) : PortfolioCommand(portfolioId)

data class WithdrawCashCommand(
        @JsonProperty("portfolioId") override val portfolioId: PortfolioId,
        @JsonProperty("amountToPayInCents") @Min(0) val amountToPayInCents: Long
) : PortfolioCommand(portfolioId)
