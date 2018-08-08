package io.pivotal.refarch.cqrs.trader.coreapi.users

data class UserByIdQuery(val userId: UserId)
data class UserByNameQuery(val userName: String)
