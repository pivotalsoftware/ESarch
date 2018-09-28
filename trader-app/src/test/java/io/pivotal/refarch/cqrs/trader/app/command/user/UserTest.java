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

import io.pivotal.refarch.cqrs.trader.app.query.users.UserAuthenticatedEvent;
import io.pivotal.refarch.cqrs.trader.app.query.users.UserCreatedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.users.AuthenticateUserCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.users.CreateUserCommand;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.*;

public class UserTest {

    private static final String USER_NAME = "Allard Buijze";
    private static final String USERNAME = "abuijze";
    private static final String PASSWORD = "extremely-difficult-password";

    private AggregateTestFixture<User> fixture;

    private UserId userId = new UserId();

    private UserCreatedEvent userCreatedEvent;

    @Before
    public void setUp() {
        fixture = new AggregateTestFixture<>(User.class);

        userCreatedEvent = new UserCreatedEvent(userId, USER_NAME, USERNAME, DigestUtils.sha1(PASSWORD));
    }

    @Test
    public void testCreateUser() {
        fixture.givenNoPriorActivity()
               .when(new CreateUserCommand(userId, USER_NAME, USERNAME, PASSWORD))
               .expectEvents(userCreatedEvent);
    }

    @Test
    public void testCreateUserThrowsIllegalArgumentExceptionForEmptyUserName() {
        fixture.givenNoPriorActivity()
               .when(new CreateUserCommand(userId, "", USERNAME, PASSWORD))
               .expectException(IllegalArgumentException.class);
    }

    @Test
    public void testCreateUserThrowsIllegalArgumentExceptionForEmptyUsername() {
        fixture.givenNoPriorActivity()
               .when(new CreateUserCommand(userId, USER_NAME, "", PASSWORD))
               .expectException(IllegalArgumentException.class);
    }

    @Test
    public void testCreateUserThrowsIllegalArgumentExceptionForEmptyPassword() {
        fixture.givenNoPriorActivity()
               .when(new CreateUserCommand(userId, USER_NAME, USERNAME, ""))
               .expectException(IllegalArgumentException.class);
    }

    @Test
    public void testAuthenticateUser() {
        fixture.given(userCreatedEvent)
               .when(new AuthenticateUserCommand(userId, USERNAME, PASSWORD.toCharArray()))
               .expectEvents(new UserAuthenticatedEvent(userId))
               .expectReturnValue(true);
    }

    @Test
    public void testAuthenticateUserReturnsFalseForNonMatchingPasswordd() {
        fixture.given(userCreatedEvent)
               .when(new AuthenticateUserCommand(userId, USERNAME, "some-other-password".toCharArray()))
               .expectNoEvents()
               .expectReturnValue(false);
    }
}
