package io.pivotal.refarch.cqrs.trader.app.query.portfolio;

import io.pivotal.refarch.cqrs.trader.app.query.users.UserView;
import io.pivotal.refarch.cqrs.trader.app.query.users.UserViewRepository;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.OrderBookId;
import io.pivotal.refarch.cqrs.trader.coreapi.orders.transaction.TransactionId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioCreatedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.PortfolioId;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.CashDepositedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.CashReservationCancelledEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.CashReservationConfirmedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.CashReservedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.portfolio.cash.CashWithdrawnEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.users.UserId;
import org.junit.*;
import org.mockito.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PortfolioMoneyEventHandlerTest {

    private static final int DEFAULT_RESERVED_AMOUNT_OF_MONEY = 1000;
    private static final int DEFAULT_AMOUNT_OF_MONEY = 10000;
    private static final String USER_NAME = "Super Rich User";

    private final PortfolioViewRepository portfolioViewRepository = mock(PortfolioViewRepository.class);
    private final UserViewRepository userViewRepository = mock(UserViewRepository.class);

    private final UserId userId = new UserId();
    private final PortfolioId portfolioId = new PortfolioId();
    private final OrderBookId itemId = new OrderBookId();
    private final CompanyId companyId = new CompanyId();
    private final TransactionId transactionId = new TransactionId();

    private ArgumentCaptor<PortfolioView> viewCaptor = ArgumentCaptor.forClass(PortfolioView.class);

    private PortfolioMoneyEventHandler testSubject;

    @Before
    public void setUp() {
        when(userViewRepository.getOne(userId.getIdentifier())).thenReturn(buildTestUser());
        when(portfolioViewRepository.getOne(portfolioId.getIdentifier())).thenReturn(buildTestPortfolio());

        testSubject = new PortfolioMoneyEventHandler(portfolioViewRepository, userViewRepository);
    }

    @Test
    public void testOnPortfolioCreatedEventAPortfolioViewIsCreated() {
        when(userViewRepository.findByIdentifier(userId.getIdentifier())).thenReturn(buildTestUser());

        long expectedAmountOfMoney = 0L;
        long expectedAmountOfReservedMoney = 0L;

        testSubject.on(new PortfolioCreatedEvent(portfolioId, userId));

        verify(userViewRepository).findByIdentifier(userId.getIdentifier());
        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        assertEquals(USER_NAME, result.getUserName());
        assertEquals(expectedAmountOfMoney, result.getAmountOfMoney());
        assertEquals(expectedAmountOfReservedMoney, result.getReservedAmountOfMoney());
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void testOnCashDepositedEventThatPortfolioAmountIsIncreased() {
        long testMoneyAddedInCents = 50L;

        long expectedAmountOfMoney = DEFAULT_AMOUNT_OF_MONEY + testMoneyAddedInCents;
        long expectedAmountOfReservedMoney = DEFAULT_RESERVED_AMOUNT_OF_MONEY;

        testSubject.on(new CashDepositedEvent(portfolioId, testMoneyAddedInCents));

        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        assertEquals(USER_NAME, result.getUserName());
        assertEquals(expectedAmountOfMoney, result.getAmountOfMoney());
        assertEquals(expectedAmountOfReservedMoney, result.getReservedAmountOfMoney());
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void testOnCashWithdrawnEventThatPortfolioAmountIsDecreased() {
        long testAmountPaidInCents = 50L;

        long expectedAmountOfMoney = DEFAULT_AMOUNT_OF_MONEY - testAmountPaidInCents;
        long expectedAmountOfReservedMoney = DEFAULT_RESERVED_AMOUNT_OF_MONEY;

        testSubject.on(new CashWithdrawnEvent(portfolioId, testAmountPaidInCents));

        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        assertEquals(USER_NAME, result.getUserName());
        assertEquals(expectedAmountOfMoney, result.getAmountOfMoney());
        assertEquals(expectedAmountOfReservedMoney, result.getReservedAmountOfMoney());
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void testOnCashReservedEventThatPortfolioReservedAmountIsIncreased() {
        long testAmountToReserve = 50L;

        long expectedAmountOfMoney = DEFAULT_AMOUNT_OF_MONEY;
        long expectedAmountOfReservedMoney = DEFAULT_RESERVED_AMOUNT_OF_MONEY + testAmountToReserve;

        testSubject.on(new CashReservedEvent(portfolioId, transactionId, testAmountToReserve));

        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        assertEquals(USER_NAME, result.getUserName());
        assertEquals(expectedAmountOfMoney, result.getAmountOfMoney());
        assertEquals(expectedAmountOfReservedMoney, result.getReservedAmountOfMoney());
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    public void testOnCashReservationCancelledEventThatPortfolioReservedAmountIsDecreased() {
        long testAmountOfMoneyToCancel = 50L;

        long expectedAmountOfMoney = DEFAULT_AMOUNT_OF_MONEY;
        long expectedAmountOfReservedMoney = DEFAULT_RESERVED_AMOUNT_OF_MONEY - testAmountOfMoneyToCancel;

        testSubject.on(new CashReservationCancelledEvent(portfolioId, transactionId, testAmountOfMoneyToCancel));

        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        assertEquals(USER_NAME, result.getUserName());
        assertEquals(expectedAmountOfMoney, result.getAmountOfMoney());
        assertEquals(expectedAmountOfReservedMoney, result.getReservedAmountOfMoney());
    }

    @Test
    public void testOnCashReservationConfirmedEventThatPortfolioReservedAndOverallAmountAreDecreased() {
        long testAmountOfMoneyConfirmedInCents = 50L;
        CashReservationConfirmedEvent testEvent =
                new CashReservationConfirmedEvent(portfolioId, transactionId, testAmountOfMoneyConfirmedInCents);

        long expectedAmountOfMoney = DEFAULT_AMOUNT_OF_MONEY - testAmountOfMoneyConfirmedInCents;
        long expectedAmountOfReservedMoney = DEFAULT_RESERVED_AMOUNT_OF_MONEY - testAmountOfMoneyConfirmedInCents;

        testSubject.on(testEvent);

        verify(portfolioViewRepository).save(viewCaptor.capture());

        PortfolioView result = viewCaptor.getValue();
        assertEquals(portfolioId.getIdentifier(), result.getIdentifier());
        assertEquals(userId.getIdentifier(), result.getUserId());
        assertEquals(USER_NAME, result.getUserName());
        assertEquals(expectedAmountOfMoney, result.getAmountOfMoney());
        assertEquals(expectedAmountOfReservedMoney, result.getReservedAmountOfMoney());
    }

    @SuppressWarnings("Duplicates")
    private PortfolioView buildTestPortfolio() {
        PortfolioView portfolioView = new PortfolioView();
        portfolioView.setIdentifier(portfolioId.getIdentifier());
        portfolioView.setUserId(userId.getIdentifier());
        portfolioView.setUserName(USER_NAME);
        portfolioView.setAmountOfMoney(DEFAULT_AMOUNT_OF_MONEY);
        portfolioView.setReservedAmountOfMoney(DEFAULT_RESERVED_AMOUNT_OF_MONEY);
        portfolioView.addItemInPossession(buildTestItem(itemId, companyId));
        portfolioView.addReservedItem(buildTestItem(itemId, companyId));
        return portfolioView;
    }

    @SuppressWarnings("Duplicates")
    private ItemEntry buildTestItem(OrderBookId itemIdentifier, CompanyId companyIdentifier) {
        ItemEntry itemInPossession = new ItemEntry();
        itemInPossession.setIdentifier(itemIdentifier.getIdentifier());
        itemInPossession.setCompanyId(companyIdentifier.getIdentifier());
        itemInPossession.setCompanyName("Test company");
        itemInPossession.setAmount(100);
        return itemInPossession;
    }

    private UserView buildTestUser() {
        UserView testView = new UserView();
        testView.setIdentifier(userId.getIdentifier());
        testView.setName(USER_NAME);
        testView.setUsername("john.doe");
        testView.setPassword("54498159823489s9fd84");
        return testView;
    }
}