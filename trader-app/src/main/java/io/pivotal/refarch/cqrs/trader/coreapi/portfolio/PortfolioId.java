package io.pivotal.refarch.cqrs.trader.coreapi.portfolio;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class PortfolioId implements Serializable {

    private static final long serialVersionUID = 5649005745169990400L;

    private final String identifier;

    public PortfolioId() {
        this(UUID.randomUUID().toString());
    }

    @JsonCreator
    public PortfolioId(String identifier) {
        Assert.notNull(identifier, "Identifier parameter may not be null");
        this.identifier = identifier;
    }

    @JsonValue
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final PortfolioId other = (PortfolioId) obj;
        return Objects.equals(this.identifier, other.identifier);
    }

    @Override
    public String toString() {
        return getIdentifier();
    }
}
