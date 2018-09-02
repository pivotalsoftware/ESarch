package io.pivotal.refarch.cqrs.trader.coreapi.portfolio

import com.fasterxml.jackson.annotation.JsonProperty
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId
import org.axonframework.commandhandling.TargetAggregateIdentifier

abstract class PortfolioCommand(@TargetAggregateIdentifier open val portfolioId: PortfolioId)

data class CreatePortfolioCommand(
        @JsonProperty("portfolioId") override val portfolioId: PortfolioId = PortfolioId(),
        @JsonProperty("userId") val userId: UserId
) : PortfolioCommand(portfolioId)
