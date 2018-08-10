package io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction

import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.OrderId
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId

data class TransactionByIdQuery(val transactionId: TransactionId)
data class TransactionByPortfolioIdQuery(val portfolioId: PortfolioId)

data class ExecutedTradesByOrderBookIdQuery(val orderBookId: OrderId)