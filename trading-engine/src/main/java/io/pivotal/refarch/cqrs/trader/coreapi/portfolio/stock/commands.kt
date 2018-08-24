package io.pivotal.refarch.cqrs.trader.coreapi.portfolio.stock

import com.fasterxml.jackson.annotation.JsonProperty
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioCommand
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId
import javax.validation.constraints.Min

data class AddItemsToPortfolioCommand(
        @JsonProperty("portfolioId") override val portfolioId: PortfolioId,
        @JsonProperty("orderBookId") val orderBookId: OrderBookId,
        @JsonProperty("amountOfItemsToAdd") @Min(0) val amountOfItemsToAdd: Long
) : PortfolioCommand(portfolioId)

data class CancelItemReservationForPortfolioCommand(
        @JsonProperty("portfolioId") override val portfolioId: PortfolioId,
        @JsonProperty("orderBookId") val orderBookId: OrderBookId,
        @JsonProperty("transactionId") val transactionId: TransactionId,
        @JsonProperty("amountOfItemsToCancel") val amountOfItemsToCancel: Long
) : PortfolioCommand(portfolioId)

data class ConfirmItemReservationForPortfolioCommand(
        @JsonProperty("portfolioId") override val portfolioId: PortfolioId,
        @JsonProperty("orderBookId") val orderBookId: OrderBookId,
        @JsonProperty("transactionId") val transactionId: TransactionId,
        @JsonProperty("amountOfItemsToConfirm") val amountOfItemsToConfirm: Long
) : PortfolioCommand(portfolioId)

data class ReserveItemsCommand(
        @JsonProperty("portfolioId")        override val portfolioId: PortfolioId,
        @JsonProperty("orderBookId") val orderBookId: OrderBookId,
        @JsonProperty("transactionId") val transactionId: TransactionId,
        @JsonProperty("amountOfItemsToReserve") val amountOfItemsToReserve: Long
) : PortfolioCommand(portfolioId)
