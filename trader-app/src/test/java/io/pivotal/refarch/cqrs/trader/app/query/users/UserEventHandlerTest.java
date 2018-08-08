package io.pivotal.refarch.cqrs.trader.app.query.users;

import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
public class UserEventHandlerTest {

    private static final String NAME = "Freddie Mercury";
    private static final String USER_NAME = "fred-da-merc";
    private static final String PASSWORD = "none-of-yo-business";

    private final UserViewRepository userViewRepository = mock(UserViewRepository.class);

    private UserEventHandler testSubject;

    private UserId testUserId;

    @Before
    public void setUp() {
        testUserId = new UserId();

        testSubject = new UserEventHandler(userViewRepository);
    }

    @Test
    public void testOnUserCreatedEventAUserViewIsSaved() {
        testSubject.on(new UserCreatedEvent(testUserId, NAME, USER_NAME, PASSWORD));
    }

    @Test
    public void testFindUserByIdQueryReturnsAUserView() {
    }

    @Test
    public void testFindUserByNameQueryReturnsAUserView() {
    }
}