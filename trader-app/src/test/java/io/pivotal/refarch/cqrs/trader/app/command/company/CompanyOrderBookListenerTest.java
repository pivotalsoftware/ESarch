package io.pivotal.refarch.cqrs.trader.app.command.company;

import io.pivotal.refarch.cqrs.trader.coreapi.company.AddOrderBookToCompanyCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyCreatedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.CreateOrderBookCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.*;
import org.mockito.*;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CompanyOrderBookListenerTest {

    private final CommandGateway commandGateway = mock(CommandGateway.class);

    private CompanyOrderBookListener testSubject = new CompanyOrderBookListener(commandGateway);

    @Test
    public void testOnCompanyCreatedEventACreatesAndAssociatesOrderBookCommandIsPublished() {
        CompanyId testCompanyId = new CompanyId();

        testSubject.on(new CompanyCreatedEvent(testCompanyId, "AxonIQ", 1337L, 50L));

        ArgumentCaptor<Object> commandCaptor = ArgumentCaptor.forClass(Object.class);

        verify(commandGateway, times(2)).send(commandCaptor.capture());

        List<Object> results = commandCaptor.getAllValues();
        CreateOrderBookCommand firstCommand = (CreateOrderBookCommand) results.get(0);
        assertNotNull(firstCommand);
        assertNotNull(firstCommand.getOrderBookId());
        AddOrderBookToCompanyCommand secondCommand = (AddOrderBookToCompanyCommand) results.get(1);
        assertNotNull(secondCommand);
        assertEquals(testCompanyId, secondCommand.getCompanyId());
        assertNotNull(secondCommand.getOrderBookId());
    }
}