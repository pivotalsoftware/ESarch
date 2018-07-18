package io.pivotal.refarch.cqrs.trader.coreapi.orders.trades

import org.axonframework.commandhandling.TargetAggregateIdentifier
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderId
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId
import javax.validation.constraints.Min

abstract class AbstractOrderCommand(
        open val orderId: OrderId,
        open val portfolioId: PortfolioId,
        @TargetAggregateIdentifier open val orderBookId: OrderBookId,
        open val transactionId: TransactionId,
        @Min(0) open val tradeCount: Long,
        @Min(0) open val itemPrice: Long
)

data class CreateBuyOrderCommand(
        override val orderId: OrderId,
        override val portfolioId: PortfolioId,
        override val orderBookId: OrderBookId,
        override val transactionId: TransactionId,
        override val tradeCount: Long,
        override val itemPrice: Long
) : AbstractOrderCommand(orderId, portfolioId, orderBookId, transactionId, tradeCount, itemPrice)

data class CreateSellOrderCommand(
        override val orderId: OrderId,
        override val portfolioId: PortfolioId,
        override val orderBookId: OrderBookId,
        override val transactionId: TransactionId,
        override val tradeCount: Long,
        override val itemPrice: Long
) : AbstractOrderCommand(orderId, portfolioId, orderBookId, transactionId, tradeCount, itemPrice)

abstract class OrderBookCommand(@TargetAggregateIdentifier open val orderBookId: OrderBookId)

data class CreateOrderBookCommand(override val orderBookId: OrderBookId) : OrderBookCommand(orderBookId)
