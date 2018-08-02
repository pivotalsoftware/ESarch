package io.pivotal.refarch.cqrs.trader.coreapi.orders.trades

import com.fasterxml.jackson.annotation.JsonProperty
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId
import org.axonframework.commandhandling.TargetAggregateIdentifier

data class CreateOrderBookCommand(
        @TargetAggregateIdentifier
        @JsonProperty("orderBookId") val orderBookId: OrderBookId
)

