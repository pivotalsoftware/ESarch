package io.pivotal.refarch.cqrs.trader.app.command.company;

import io.pivotal.refarch.cqrs.trader.coreapi.company.AddOrderBookToCompanyCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyCreatedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CreateCompanyCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.company.OrderBookAddedToCompanyEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.*;

public class CompanyTest {

    private static final String COMPANY_NAME = "AxonIQ";
    private static final long COMPANY_VALUE = 1000L;
    private static final long AMOUNT_OF_SHARES = 10000L;

    private AggregateTestFixture<Company> fixture;

    private CompanyId companyId = new CompanyId();
    private OrderBookId orderBookId = new OrderBookId();

    private CompanyCreatedEvent companyCreatedEvent;

    @Before
    public void setUp() {
        fixture = new AggregateTestFixture<>(Company.class);

        companyCreatedEvent = new CompanyCreatedEvent(companyId, COMPANY_NAME, COMPANY_VALUE, AMOUNT_OF_SHARES);
    }

    @Test
    public void testCreateCompany() {
        fixture.givenNoPriorActivity()
               .when(new CreateCompanyCommand(companyId, COMPANY_NAME, COMPANY_VALUE, AMOUNT_OF_SHARES))
               .expectEvents(companyCreatedEvent);
    }

    @Test
    public void testCreateCompanyThrowsIllegalArgumentExceptionForNegativeCompanyValue() {
        fixture.givenNoPriorActivity()
               .when(new CreateCompanyCommand(companyId, COMPANY_NAME, -10L, AMOUNT_OF_SHARES))
               .expectException(IllegalArgumentException.class);
    }

    @Test
    public void testCreateCompanyThrowsIllegalArgumentExceptionForNegativeAmountOfShares() {
        fixture.givenNoPriorActivity()
               .when(new CreateCompanyCommand(companyId, COMPANY_NAME, COMPANY_VALUE, -10L))
               .expectException(IllegalArgumentException.class);
    }

    @Test
    public void testAddOrderBookToCompany() {
        fixture.given(companyCreatedEvent)
               .when(new AddOrderBookToCompanyCommand(companyId, orderBookId))
               .expectEvents(new OrderBookAddedToCompanyEvent(companyId, orderBookId));
    }
}
