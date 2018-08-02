package io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId
import io.pivotal.refarch.cqrs.trader.coreapi.orders.TransactionType
import org.axonframework.commandhandling.TargetAggregateIdentifier

abstract class TransactionCommand(@TargetAggregateIdentifier open val transactionId: TransactionId)

abstract class AbstractStartTransactionCommand(
        @JsonProperty("transactionId") override val transactionId: TransactionId,
        @JsonProperty("orderBookId") open val orderBookId: OrderBookId,
        @JsonProperty("portfolioId") open val portfolioId: PortfolioId,
        @JsonProperty("tradeCount") open val tradeCount: Long,
        @JsonProperty("pricePerItem") open val pricePerItem: Long
) : TransactionCommand(transactionId) {
    abstract val transactionType: TransactionType
}

data class StartBuyTransactionCommand(
        override val transactionId: TransactionId,
        override val orderBookId: OrderBookId,
        override val portfolioId: PortfolioId,
        override val tradeCount: Long,
        override val pricePerItem: Long
) : AbstractStartTransactionCommand(transactionId, orderBookId, portfolioId, tradeCount, pricePerItem) {
    @JsonIgnore override val transactionType: TransactionType = TransactionType.BUY
}

data class StartSellTransactionCommand(
        override val transactionId: TransactionId,
        override val orderBookId: OrderBookId,
        override val portfolioId: PortfolioId,
        override val tradeCount: Long,
        override val pricePerItem: Long
) : AbstractStartTransactionCommand(transactionId, orderBookId, portfolioId, tradeCount, pricePerItem) {
    @JsonIgnore override val transactionType: TransactionType = TransactionType.SELL
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
