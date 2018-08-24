package io.pivotal.refarch.cqrs.trader.app.contracts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import io.pivotal.refarch.cqrs.trader.app.controller.CommandController;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CreateCompanyCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.trades.CreateOrderBookCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.StartBuyTransactionCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.StartSellTransactionCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.CreatePortfolioCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.users.CreateUserCommand;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.*;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CommandContractTest {

    private static final String COMPANY_ID = "f82c4dd0-a785-11e8-98d0-529269fb1459";
    private static final String ORDER_BOOK_ID = "f82c40ec-a785-11e8-98d0-529269fb1459";
    private static final String PORTFOLIO_ID = "f82c481c-a785-11e8-98d0-529269fb1459";
    private static final String USER_ID = "98684ad8-987e-4d16-8ad8-b620f4320f4c";
    private static final String BUY_TRANSACTION_ID = "f82c4984-a785-11e8-98d0-529269fb1459";
    private static final String SELL_TRANSACTION_ID = "f82c4ace-a785-11e8-98d0-529269fb1459";

    @Before
    public void setup() {
        final CommandGateway commandGateway = mock(CommandGateway.class);

        when(commandGateway.send(any())).thenReturn(completedFuture(null));
        when(commandGateway.send(any(CreateCompanyCommand.class)))
                .thenReturn(completedFuture(COMPANY_ID));
        when(commandGateway.send(any(CreateOrderBookCommand.class)))
                .thenReturn(completedFuture(ORDER_BOOK_ID));
        when(commandGateway.send(any(CreatePortfolioCommand.class)))
                .thenReturn(completedFuture(PORTFOLIO_ID));
        when(commandGateway.send(any(CreateUserCommand.class)))
                .thenReturn(completedFuture(USER_ID));
        when(commandGateway.send(any(StartBuyTransactionCommand.class)))
                .thenReturn(completedFuture(BUY_TRANSACTION_ID));
        when(commandGateway.send(any(StartSellTransactionCommand.class)))
                .thenReturn(completedFuture(SELL_TRANSACTION_ID));

        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(objectMapper);

        final CommandController commandController =
                new CommandController(commandGateway, objectMapper, jsonSchemaGenerator);
        commandController.setBeanClassLoader(this.getClass().getClassLoader());

        RestAssuredMockMvc.standaloneSetup(commandController);
    }
}
