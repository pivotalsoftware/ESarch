package io.pivotal.catalog.controllers;


import io.pivotal.catalog.commands.AddProductToCatalogCommand;
import io.pivotal.catalog.services.CatalogService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class CatalogApiControllerTest {

    @MockBean
    CatalogService commandGateway;
    CatalogApiController controller;

    Map<String, String> request;
    String id;
    String name;

    @Before
    public void init(){
        controller = new CatalogApiController(commandGateway);
        id = UUID.randomUUID().toString();
        name = "test-"+id;
        request = new HashMap<String, String>();
        request.put("id", id);
        request.put("name", name);
    }


    @Test
    public void checkControllerCallsServiceCorrectly() throws ExecutionException, InterruptedException {

        assertNotNull(request);
        assertNotNull(request.containsKey("id"));
        assertNotNull(request.containsKey("name"));
        // Arrange

        when(commandGateway.addProductToCatalog(any(AddProductToCatalogCommand.class)))
                .thenAnswer(i -> {
                    AddProductToCatalogCommand command = i.getArgumentAt(0, AddProductToCatalogCommand.class);
                    CompletableFuture<String> response =  new CompletableFuture<String>();
                    response.complete(command.getId());
                    return response;
                });

        // Act
        CompletableFuture<String> answer = controller.addProductToCatalog(request);

        // Assert
        verify(commandGateway, times(1)).addProductToCatalog(any(AddProductToCatalogCommand.class));
        verifyNoMoreInteractions(commandGateway);
        assertEquals(id, answer.get().toString());
    }


}
