package io.pivotal.refarch.cqrs.trader.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import io.pivotal.refarch.cqrs.trader.app.controller.CommandController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Before;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseContractTest {

  @Before
  public void setup(){

    final CommandGateway commandGateway = mock(CommandGateway.class);
    final CompletableFuture<Object> mockCompletableFuture = mock(CompletableFuture.class);

    final ObjectMapper objectMapper = new ObjectMapper();
    final JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(objectMapper);

    final CommandController commandController = new CommandController(commandGateway,objectMapper,jsonSchemaGenerator);
    final ClassLoader classLoader = this.getClass().getClassLoader();

    commandController.setBeanClassLoader(classLoader);
    when(commandGateway.send(any())).thenReturn(mockCompletableFuture);
    RestAssuredMockMvc.standaloneSetup(commandController);
  }
}
