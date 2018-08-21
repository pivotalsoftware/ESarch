package io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction

import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId

data class TransactionByIdQuery(val transactionId: TransactionId)
data class TransactionsByPortfolioIdQuery(val portfolioId: PortfolioId)

data class ExecutedTradesByOrderBookIdQuery(val orderBookId: OrderBookId)