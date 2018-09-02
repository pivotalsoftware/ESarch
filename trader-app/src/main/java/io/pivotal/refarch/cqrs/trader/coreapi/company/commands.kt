package io.pivotal.refarch.cqrs.trader.coreapi.company

import com.fasterxml.jackson.annotation.JsonProperty
import org.axonframework.commandhandling.TargetAggregateIdentifier
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId

abstract class CompanyCommand(@TargetAggregateIdentifier open val companyId: CompanyId)

data class CreateCompanyCommand(
        @JsonProperty("companyId") override val companyId: CompanyId = CompanyId(),
        @JsonProperty("userId") val userId: UserId,
        @JsonProperty("companyName") val companyName: String,
        @JsonProperty("companyValue") val companyValue: Long,
        @JsonProperty("amountOfShares") val amountOfShares: Long
) : CompanyCommand(companyId)

data class AddOrderBookToCompanyCommand(
        @JsonProperty("companyId") override val companyId: CompanyId,
        @JsonProperty("orderBookId") val orderBookId: OrderBookId
) : CompanyCommand(companyId)
