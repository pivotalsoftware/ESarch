/*
 * Copyright (c) 2010-2012. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.refarch.cqrs.trader.app.command.user;

import io.pivotal.refarch.cqrs.trader.app.query.users.UserCreatedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.CreatePortfolioCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.*;
import org.mockito.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PortfolioManagementUserListenerTest {

    private final CommandGateway commandGateway = mock(CommandGateway.class);

    private final PortfolioManagementUserListener testSubject = new PortfolioManagementUserListener(commandGateway);

    @Test
    public void testOnUserCreatedEventACreatePortfolioCommandIsPublished() {
        UserId testUserId = new UserId();

        testSubject.on(new UserCreatedEvent(testUserId, "Test", "testuser", "testpassword"));

        ArgumentCaptor<CreatePortfolioCommand> commandCaptor = ArgumentCaptor.forClass(CreatePortfolioCommand.class);

        verify(commandGateway).send(commandCaptor.capture());

        CreatePortfolioCommand result = commandCaptor.getValue();
        assertNotNull(result);
        assertNotNull(result.getPortfolioId());
        assertEquals(testUserId, result.getUserId());
    }
}
