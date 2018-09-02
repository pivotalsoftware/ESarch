package io.pivotal.refarch.cqrs.trader.coreapi.company

data class CompanyByIdQuery(val companyId: CompanyId)

data class CompanyByNameQuery(val companyName: String)