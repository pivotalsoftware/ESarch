package io.pivotal.refarch.cqrs.trader.coreapi.portfolio

import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId

abstract class PortfolioEvent(open val portfolioId: PortfolioId)

class PortfolioCreatedEvent(
        override val portfolioId: PortfolioId,
        val userId: UserId
) : PortfolioEvent(portfolioId)
