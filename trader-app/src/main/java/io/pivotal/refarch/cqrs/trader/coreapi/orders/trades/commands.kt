package io.pivotal.refarch.cqrs.trader.coreapi.orders.trades

import com.fasterxml.jackson.annotation.JsonProperty
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderId
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId
import org.axonframework.commandhandling.TargetAggregateIdentifier
import javax.validation.constraints.Min

abstract class OrderBookCommand(@TargetAggregateIdentifier open val orderBookId: OrderBookId)

data class CreateOrderBookCommand(
        @JsonProperty("orderBookId") override val orderBookId: OrderBookId
) : OrderBookCommand(orderBookId)

abstract class OrderCommand(
        @JsonProperty("orderBookId") override val orderBookId: OrderBookId,
        @JsonProperty("orderId") open val orderId: OrderId,
        @JsonProperty("portfolioId") open val portfolioId: PortfolioId,
        @JsonProperty("transactionId") open val transactionId: TransactionId,
        @JsonProperty("tradeCount") @Min(0) open val tradeCount: Long,
        @JsonProperty("itemPrice") @Min(0) open val itemPrice: Long
) : OrderBookCommand(orderBookId)

data class CreateBuyOrderCommand(
        override val orderId: OrderId,
        override val portfolioId: PortfolioId,
        override val orderBookId: OrderBookId,
        override val transactionId: TransactionId,
        override val tradeCount: Long,
        override val itemPrice: Long
) : OrderCommand(orderBookId, orderId, portfolioId, transactionId, tradeCount, itemPrice)

data class CreateSellOrderCommand(
        override val orderId: OrderId,
        override val portfolioId: PortfolioId,
        override val orderBookId: OrderBookId,
        override val transactionId: TransactionId,
        override val tradeCount: Long,
        override val itemPrice: Long
) : OrderCommand(orderBookId, orderId, portfolioId, transactionId, tradeCount, itemPrice)
