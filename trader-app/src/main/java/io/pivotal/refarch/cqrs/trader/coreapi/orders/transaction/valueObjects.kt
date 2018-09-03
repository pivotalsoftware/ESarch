package io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction

enum class TransactionState {
    STARTED, CONFIRMED, CANCELLED, EXECUTED, PARTIALLY_EXECUTED
}
