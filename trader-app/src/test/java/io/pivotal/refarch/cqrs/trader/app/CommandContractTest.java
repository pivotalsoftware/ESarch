package io.pivotal.refarch.cqrs.trader.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import io.pivotal.refarch.cqrs.trader.app.controller.CommandController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.*;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CommandContractTest {

    private static final String EXPECTED_UUID = "expected-uuid";

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        final CommandGateway commandGateway = mock(CommandGateway.class);
        final CompletableFuture<Object> mockCompletableFuture = mock(CompletableFuture.class);

        when(mockCompletableFuture.thenApply(ResponseEntity::ok))
                .thenReturn(CompletableFuture.completedFuture(ResponseEntity.ok(EXPECTED_UUID)));
        when(commandGateway.send(any())).thenReturn(mockCompletableFuture);

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(objectMapper);

        final CommandController commandController =
                new CommandController(commandGateway, objectMapper, jsonSchemaGenerator);
        commandController.setBeanClassLoader(this.getClass().getClassLoader());

        RestAssuredMockMvc.standaloneSetup(commandController);
    }
}
