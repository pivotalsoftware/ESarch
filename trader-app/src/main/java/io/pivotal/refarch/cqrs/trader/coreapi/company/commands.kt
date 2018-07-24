package io.pivotal.refarch.cqrs.trader.app.coreapi.company

import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId
import org.axonframework.commandhandling.TargetAggregateIdentifier
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId

abstract class CompanyCommand(@TargetAggregateIdentifier open val companyId: CompanyId)

data class CreateCompanyCommand(
        override val companyId: CompanyId,
        val userId: UserId,
        val companyName: String,
        val companyValue: Long,
        val amountOfShares: Long
) : CompanyCommand(companyId)

data class AddOrderBookToCompanyCommand(
        override val companyId: CompanyId,
        val orderBookId: OrderBookId
) : CompanyCommand(companyId)
