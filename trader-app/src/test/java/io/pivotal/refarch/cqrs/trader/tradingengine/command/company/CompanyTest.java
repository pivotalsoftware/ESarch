package io.pivotal.refarch.cqrs.trader.tradingengine.command.company;

import io.pivotal.refarch.cqrs.trader.coreapi.company.*;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;
import org.junit.Test;

public class CompanyTest {

    private AggregateTestFixture<Company> fixture;

    private CompanyId companyId = new CompanyId();
    private OrderBookId orderBookId = new OrderBookId();

    private CompanyCreatedEvent companyCreatedEvent;

    @Before
    public void setUp() {
        fixture = new AggregateTestFixture<>(Company.class);

        companyCreatedEvent = new CompanyCreatedEvent(companyId, "TestItem", 1000L, 10000L);
    }

    @Test
    public void testCreateCompany() {
        fixture.givenNoPriorActivity()
               .when(new CreateCompanyCommand(companyId, new UserId(), "TestItem", 1000L, 10000L))
               .expectEvents(companyCreatedEvent);
    }

    @Test
    public void testAddOrderBookToCompany() {
        fixture.given(companyCreatedEvent)
               .when(new AddOrderBookToCompanyCommand(companyId, orderBookId))
               .expectEvents(new OrderBookAddedToCompanyEvent(companyId, orderBookId));
    }
}
