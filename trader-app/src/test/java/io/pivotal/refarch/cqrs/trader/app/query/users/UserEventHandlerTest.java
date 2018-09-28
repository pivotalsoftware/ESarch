package io.pivotal.refarch.cqrs.trader.app.query.users;

import io.pivotal.refarch.cqrs.trader.coreapi.users.FindAllUsersQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserByIdQuery;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
    public void testFindUserByIdReturnsAUserView() {
        UserView testView = buildTestView();

        when(userViewRepository.findByIdentifier(testUserId.getIdentifier())).thenReturn(testView);

        UserView result = testSubject.find(new UserByIdQuery(testUserId));

        assertEquals(testView, result);
    }

    @Test
    public void testFindUserByIdReturnsNullForNonExistingUserId() {
        when(userViewRepository.findByIdentifier(testUserId.getIdentifier())).thenReturn(null);

        assertNull(testSubject.find(new UserByIdQuery(testUserId)));
    }

    @Test
    public void testFindAllUserReturnsAListOfUserViews() {
        UserView testView = buildTestView();

        when(userViewRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.singletonList(testView)));

        List<UserView> result = testSubject.findAll(new FindAllUsersQuery(0, 50));

        assertEquals(testView, result.get(0));
    }

    private UserView buildTestView() {
        UserView testView = new UserView();
        testView.setIdentifier(testUserId.getIdentifier());
        testView.setName(NAME);
        testView.setUsername(USERNAME);
        return testView;
    }
}
