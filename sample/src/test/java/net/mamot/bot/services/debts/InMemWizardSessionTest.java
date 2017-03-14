package net.mamot.bot.services.debts;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Mockito.*;

@Ignore
public class InMemWizardSessionTest {
    WizardStep someStep = mock(WizardStep.class);
    WizardSession sut = new InMemWizardSession();

    @Before
    public void setUp() throws Exception {
        sut.start(1, someStep);
    }

    @Test
    public void sessionStartShouldShowsStep() throws Exception {
        verify(someStep).show();
        verifyNoMoreInteractions(someStep);
    }

    @Test
    public void showShouldShowsStep() throws Exception {
        sut.show(1);

        verify(someStep, times(2)).show();
        verifyNoMoreInteractions(someStep);
    }

    @Test
    public void shouldCallCallbackForStep() throws Exception {
        sut.callback(1, "data");

        verify(someStep).callback("data");
        verify(someStep).show();
        verifyNoMoreInteractions(someStep);
    }

    @Test
    public void shouldCallEnterForStep() throws Exception {
        sut.enter(1, "data");

        verify(someStep).show();
        verify(someStep).enter("data");
        verifyNoMoreInteractions(someStep);
    }
}