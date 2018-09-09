package io.pivotal.refarch.cqrs.trader.coreapi.company

data class CompanyByIdQuery(val companyId: CompanyId)
data class FindAllCompaniesQuery(val pageOffset: Int, val pageSize: Int)

