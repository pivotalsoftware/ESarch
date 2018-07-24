package io.pivotal.refarch.cqrs.trader.app.query.users

import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId

abstract class UserEvent(open val userId: UserId)

data class UserCreatedEvent(
        override val userId: UserId,
        val name: String,
        val username: String,
        val password: String
) : UserEvent(userId)

data class UserAuthenticatedEvent(override val userId: UserId) : UserEvent(userId)
