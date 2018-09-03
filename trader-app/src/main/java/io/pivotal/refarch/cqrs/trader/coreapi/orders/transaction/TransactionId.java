package io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class TransactionId implements Serializable {

    private static final long serialVersionUID = -5267104328616955617L;

    private final String identifier;

    public TransactionId() {
        this(UUID.randomUUID().toString());
    }

    public TransactionId(String identifier) {
        Assert.notNull(identifier, "identifier may not be null");
        this.identifier = identifier;
    }

    @JsonValue
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionId that = (TransactionId) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
