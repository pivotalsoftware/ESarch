package io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction;

public enum TransactionState {
    STARTED, CONFIRMED, CANCELLED, EXECUTED, PARTIALLY_EXECUTED
}
