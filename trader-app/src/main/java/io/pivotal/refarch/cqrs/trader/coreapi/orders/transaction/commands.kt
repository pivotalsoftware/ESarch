package io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId
import io.pivotal.refarch.cqrs.trader.coreapi.orders.TransactionType
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId
import org.axonframework.commandhandling.TargetAggregateIdentifier

abstract class TransactionCommand(@TargetAggregateIdentifier open val transactionId: TransactionId)

abstract class AbstractStartTransactionCommand(
        override val transactionId: TransactionId,
        open val orderBookId: OrderBookId,
        open val portfolioId: PortfolioId,
        open val tradeCount: Long,
        open val pricePerItem: Long
) : TransactionCommand(transactionId) {
    abstract val transactionType: TransactionType
}

data class StartBuyTransactionCommand(
        @JsonProperty("transactionId") override val transactionId: TransactionId,
        @JsonProperty("orderBookId") override val orderBookId: OrderBookId,
        @JsonProperty("portfolioId") override val portfolioId: PortfolioId,
        @JsonProperty("tradeCount") override val tradeCount: Long,
        @JsonProperty("pricePerItem") override val pricePerItem: Long
) : AbstractStartTransactionCommand(transactionId, orderBookId, portfolioId, tradeCount, pricePerItem) {
    @JsonIgnore
    override val transactionType: TransactionType = TransactionType.BUY
}

data class StartSellTransactionCommand(
        @JsonProperty("transactionId") override val transactionId: TransactionId,
        @JsonProperty("orderBookId") override val orderBookId: OrderBookId,
        @JsonProperty("portfolioId") override val portfolioId: PortfolioId,
        @JsonProperty("tradeCount") override val tradeCount: Long,
        @JsonProperty("pricePerItem") override val pricePerItem: Long
) : AbstractStartTransactionCommand(transactionId, orderBookId, portfolioId, tradeCount, pricePerItem) {
    @JsonIgnore
    override val transactionType: TransactionType = TransactionType.SELL
}

data class CancelTransactionCommand(
        @JsonProperty("transactionId") override val transactionId: TransactionId
) : TransactionCommand(transactionId)

data class ConfirmTransactionCommand(
        @JsonProperty("transactionId") override val transactionId: TransactionId
) : TransactionCommand(transactionId)

data class ExecutedTransactionCommand(
        @JsonProperty("transactionId") override val transactionId: TransactionId,
        @JsonProperty("amountOfItems") val amountOfItems: Long,
        @JsonProperty("itemPrice") val itemPrice: Long
) : TransactionCommand(transactionId)
