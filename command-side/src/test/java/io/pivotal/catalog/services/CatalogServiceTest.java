package io.pivotal.catalog.services;

import io.pivotal.catalog.commands.AddProductToCatalogCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class CatalogServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogServiceTest.class);

    private String id;
    private String name;
    private AddProductToCatalogCommand command;

    @MockBean
    private CommandGateway commandGateway;

    private CatalogService service;


    @Before
    public void init() {
        id = UUID.randomUUID().toString();
        name = "test-" + id;
        command = new AddProductToCatalogCommand(id, name);
        service = new CatalogService(commandGateway);
    }

    @Test
    public void testApi() throws Exception {
        //Arrange
        when(commandGateway.send(any()))
                .thenAnswer(i -> {
                    AddProductToCatalogCommand command = i.getArgumentAt(0, AddProductToCatalogCommand.class);
                    assertEquals(id, command.getId());
                    CompletableFuture<String> response = new CompletableFuture<String>();
                    response.complete(command.getId());
                    return response;
                });

        //Act
        CompletableFuture<String> response = service.addProductToCatalog(command);

        //Assert
        verify(commandGateway, times(1)).send(any());
        verifyNoMoreInteractions(commandGateway);
        assertEquals(id, response.get().toString());
    }

    public String getId() {
        return id;
    }

}
