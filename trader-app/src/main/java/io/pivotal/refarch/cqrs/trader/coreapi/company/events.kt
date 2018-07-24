package io.pivotal.refarch.cqrs.trader.app.coreapi.company

import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId

abstract class CompanyEvent(open val companyId: CompanyId)

data class CompanyCreatedEvent(
        override val companyId: CompanyId,
        val companyName: String,
        val companyValue: Long,
        val amountOfShares: Long
) : CompanyEvent(companyId)

data class OrderBookAddedToCompanyEvent(
        override val companyId: CompanyId,
        val orderBookId: OrderBookId
) : CompanyEvent(companyId)
