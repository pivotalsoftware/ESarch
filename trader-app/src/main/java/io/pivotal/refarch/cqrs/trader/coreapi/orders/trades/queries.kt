package io.pivotal.refarch.cqrs.trader.coreapi.orders.trades

import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId

data class OrderBookViewQuery(val companyId : CompanyId)
