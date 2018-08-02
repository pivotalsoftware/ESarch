package io.pivotal.refarch.cqrs.trader.coreapi.orders

import com.fasterxml.jackson.annotation.JsonValue
import org.axonframework.common.IdentifierFactory
import java.io.Serializable

enum class TransactionType {
    SELL, BUY
}

data class OrderBookId(@JsonValue val identifier: String = IdentifierFactory.getInstance().generateIdentifier()) : Serializable

