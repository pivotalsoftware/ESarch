package io.pivotal.refarch.cqrs.trader.query.company;

import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyCreatedEvent;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyId;
import io.pivotal.refarch.cqrs.trader.coreapi.company.CompanyView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class CompanyEventHandlerTest {

    private final CompanyViewRepository companyViewRepository = mock(CompanyViewRepository.class);

    private CompanyEventHandler testSubject;

    @Before
    public void setUp() {
        testSubject = new CompanyEventHandler(companyViewRepository);
    }

    @Test
    public void testOnCompanyCreatedEventACompanyViewIsSaved() {
        CompanyId expectedCompanyId = new CompanyId();
        String expectedCompanyName = "companyName";
        int expectedCompanyValue = 1000;
        int expectedAmountOfShares = 500;

        CompanyCreatedEvent testEvent = new CompanyCreatedEvent(
                expectedCompanyId, expectedCompanyName, expectedCompanyValue, expectedAmountOfShares
        );

        testSubject.on(testEvent);

        ArgumentCaptor<CompanyView> companyViewCaptor = ArgumentCaptor.forClass(CompanyView.class);

        verify(companyViewRepository).save(companyViewCaptor.capture());

        CompanyView result = companyViewCaptor.getValue();
        assertNotNull(result);
        assertEquals(expectedCompanyId.getIdentifier(), result.getIdentifier());
        assertEquals(expectedCompanyName, result.getName());
        assertEquals(expectedCompanyValue, result.getValue());
        assertEquals(expectedAmountOfShares, result.getAmountOfShares());
    }
}
