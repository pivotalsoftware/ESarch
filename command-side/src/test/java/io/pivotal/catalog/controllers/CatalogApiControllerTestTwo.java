package io.pivotal.catalog.controllers;


import com.google.gson.Gson;
import io.pivotal.catalog.commands.AddProductToCatalogCommand;
import io.pivotal.catalog.services.CatalogService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CatalogApiController.class)
public class CatalogApiControllerTestTwo {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogService service;

    String id;
    String name;
    Map<String, String> request;
    String json;

    @Before
    public void init(){
        id = UUID.randomUUID().toString();
        name = "test-"+id;
        request = new HashMap<>();
        request.put("id", id);
        request.put("name", name);
        Gson gson = new Gson();
        json = gson.toJson(request);
    }

    @Test
    public void contextLoads(){
        assertNotNull(mockMvc);
        assertNotNull(service);
    }

    @Test
    public void shouldAddProductUsingCommand() throws Exception {

        // Arrange
        when(service.addProductToCatalog(any(AddProductToCatalogCommand.class)))
                .thenAnswer(i -> {
                    CompletableFuture<String> response = new CompletableFuture<>();
                    response.complete(id);
                    return response;
                });

        // Act
        MvcResult result = this.mockMvc.perform(post("/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andReturn();

        this.mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().string(id));

        // Assert
        verify(service, times(1)).addProductToCatalog(any(AddProductToCatalogCommand.class));
        verifyNoMoreInteractions(service);

    }
}
