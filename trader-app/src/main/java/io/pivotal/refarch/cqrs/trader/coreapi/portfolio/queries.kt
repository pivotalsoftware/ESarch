package io.pivotal.refarch.cqrs.trader.coreapi.portfolio

import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId

data class PortfolioByIdQuery(val portfolioId: PortfolioId)
data class PortfolioByUserIdQuery(val userId: UserId)
