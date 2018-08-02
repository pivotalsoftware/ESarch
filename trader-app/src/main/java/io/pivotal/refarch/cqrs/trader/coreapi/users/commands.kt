package io.pivotal.refarch.cqrs.trader.app.query.users

import com.fasterxml.jackson.annotation.JsonProperty
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

abstract class UserCommand(@TargetAggregateIdentifier open val userId: UserId)

class CreateUserCommand(
        @JsonProperty("userId") override val userId: UserId,
        @JsonProperty("name") val name: String, @NotNull @Size(min = 3)
        @JsonProperty("username") val username: String, @NotNull @Size(min = 3)
        @JsonProperty("password") val password: String
) : UserCommand(userId)

data class AuthenticateUserCommand(
        @JsonProperty("userId") override val userId: UserId,
        @JsonProperty("userName") val userName: String,
        @JsonProperty("password") @NotNull @Size(min = 3) val password: CharArray
) : UserCommand(userId) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AuthenticateUserCommand) return false

        if (userId != other.userId) return false
        if (userName != other.userName) return false
        if (!Arrays.equals(password, other.password)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + Arrays.hashCode(password)
        return result
    }

}
