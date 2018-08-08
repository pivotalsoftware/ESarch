package io.pivotal.refarch.cqrs.trader.coreapi.orders.trades

import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId

data class OrderBookByIdQuery(val orderBookId: OrderBookId)
data class OrderBookByCompanyIdQuery(val companyId : CompanyId)