package io.pivotal.refarch.cqrs.trader.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.CreateOrderBookCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.CreatePortfolioCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CommandControllerTest {

    private final CommandGateway commandGateway = mock(CommandGateway.class);
    private final ObjectMapper objectMapper = mock(ObjectMapper.class);
    private final JsonSchemaGenerator jsonSchemaGenerator = mock(JsonSchemaGenerator.class);

    private CommandController testSubject;
    private String expectedCommandSchemaString;
    private ObjectSchema commandSchema;

    @Before
    public void setUp() throws Exception {
        expectedCommandSchemaString = "command-schema";
        commandSchema = new ObjectSchema();
        when(jsonSchemaGenerator.generateSchema(any(Class.class))).thenReturn(commandSchema);
        when(objectMapper.writeValueAsString(commandSchema)).thenReturn(expectedCommandSchemaString);

        testSubject = new CommandController(commandGateway, objectMapper, jsonSchemaGenerator);
        testSubject.setBeanClassLoader(this.getClass().getClassLoader());
    }

    @Test
    public void testGetCommandApiGeneratesAndReturnsApi() throws Exception {
        int expectedNumberOfCommandApis = 20;

        Map<String, String> result = testSubject.getCommandApi();

        assertEquals(expectedNumberOfCommandApis, result.size());
        result.values().forEach(resultCommandSchema -> assertEquals(expectedCommandSchemaString, resultCommandSchema));

        verify(jsonSchemaGenerator, times(expectedNumberOfCommandApis)).generateSchema(any(Class.class));
        verify(objectMapper, times(expectedNumberOfCommandApis)).writeValueAsString(commandSchema);
    }

    @Test
    public void testPostCommandReturnsNotFoundForNonExistingCommand() throws ExecutionException, InterruptedException {
        CompletableFuture<ResponseEntity> futureResult = testSubject.postCommand("non-existing-command", "irrelevant");

        assertTrue(futureResult.isDone());
        assertEquals(HttpStatus.NOT_FOUND, futureResult.get().getStatusCode());
    }

    @Test
    public void testPostCommandReturnsNotFoundIfItCannotSerializeTheJsonCommand() throws Exception {
        Class<CreatePortfolioCommand> testCommandType = CreatePortfolioCommand.class;
        String faultyCommandJson = "{some-broken-json}";

        when(objectMapper.readValue(faultyCommandJson, testCommandType)).thenThrow(new IOException());

        CompletableFuture<ResponseEntity> futureResult =
                testSubject.postCommand(testCommandType.getSimpleName(), faultyCommandJson);

        assertTrue(futureResult.isDone());
        assertEquals(HttpStatus.NOT_FOUND, futureResult.get().getStatusCode());
    }

    @Test
    public void testPostCommandPublishesTheCommand() throws Exception {
        CreatePortfolioCommand testCmd = new CreatePortfolioCommand(new PortfolioId(), new UserId());
        ObjectMapper realMapper = new ObjectMapper();
        String testCommandJson = realMapper.writeValueAsString(testCmd);

        when(objectMapper.readValue(testCommandJson, CreatePortfolioCommand.class)).thenReturn(testCmd);
        when(commandGateway.send(any())).thenReturn(CompletableFuture.completedFuture("irrelevant"));

        CompletableFuture<ResponseEntity> futureResult =
                testSubject.postCommand(testCmd.getClass().getSimpleName(), testCommandJson);

        assertTrue(futureResult.isDone());
        assertEquals(HttpStatus.OK, futureResult.get().getStatusCode());

        verify(objectMapper).readValue(testCommandJson, testCmd.getClass());
        verify(commandGateway).send(testCmd);
    }

    @Test
    public void testPostCommandPublishesTheCommandForAnEmptyCommandPayloadIfTheCommandAllowsIt() throws Exception {
        CreateOrderBookCommand testCmd = new CreateOrderBookCommand();

        when(commandGateway.send(any())).thenReturn(CompletableFuture.completedFuture("irrelevant"));

        CompletableFuture<ResponseEntity> futureResult =
                testSubject.postCommand(testCmd.getClass().getSimpleName(), null);

        assertTrue(futureResult.isDone());
        assertEquals(HttpStatus.OK, futureResult.get().getStatusCode());

        // The command id will be automatically generated and thus is different every time, hence we do not assert it.
        verify(commandGateway).send(any(CreateOrderBookCommand.class));
    }
}
