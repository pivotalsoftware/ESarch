package io.pivotal.refarch.cqrs.trader.coreapi.portfolio

import org.axonframework.commandhandling.TargetAggregateIdentifier
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId

abstract class PortfolioCommand(@TargetAggregateIdentifier open val portfolioId: PortfolioId)

data class CreatePortfolioCommand(
        override val portfolioId: PortfolioId,
        val userId: UserId
) : PortfolioCommand(portfolioId)
