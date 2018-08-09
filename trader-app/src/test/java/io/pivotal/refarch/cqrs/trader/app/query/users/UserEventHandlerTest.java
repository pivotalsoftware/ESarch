package io.pivotal.refarch.cqrs.trader.app.query.users;

import io.pivotal.refarch.cqrs.trader.coreapi.users.UserByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserByNameQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class UserEventHandlerTest {

    private static final String NAME = "Freddie Mercury";
    private static final String USERNAME = "fred-da-merc";
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
        testSubject.on(new UserCreatedEvent(testUserId, NAME, USERNAME, PASSWORD));

        ArgumentCaptor<UserView> argumentCaptor = ArgumentCaptor.forClass(UserView.class);

        verify(userViewRepository).save(argumentCaptor.capture());

        UserView result = argumentCaptor.getValue();
        assertNotNull(result);
        assertEquals(testUserId.getIdentifier(), result.getIdentifier());
        assertEquals(NAME, result.getName());
        assertEquals(USERNAME, result.getUsername());
    }

    @Test
    public void testFindUserByIdQueryReturnsAUserView() {
        UserView testView = buildTestView();

        when(userViewRepository.findByIdentifier(testUserId.getIdentifier())).thenReturn(testView);

        UserView result = testSubject.find(new UserByIdQuery(testUserId));

        assertEquals(testView, result);
    }

    @Test
    public void testFindUserByNameQueryReturnsAUserView() {
        UserView testView = buildTestView();

        when(userViewRepository.findByUsername(NAME)).thenReturn(testView);

        UserView result = testSubject.find(new UserByNameQuery(NAME));

        assertEquals(testView, result);
    }

    private UserView buildTestView() {
        UserView testView = new UserView();
        testView.setIdentifier(testUserId.getIdentifier());
        testView.setName(NAME);
        testView.setUsername(USERNAME);
        testView.setPassword(PASSWORD);
        return testView;
    }
}