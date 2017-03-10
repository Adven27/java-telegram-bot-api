package net.mamot.bot.services.debts;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@Ignore
public class InMemWizardSessionTest {
    WizardStep initStep = mock(WizardStep.class);
    WizardStep someStep = new WhoStep(new Transaction(1));
    WizardSession sut = new InMemWizardSession();

    @Test
    public void getShouldReturnInitialStepIfThereIsNoSetted() throws Exception {
        assertEquals(initStep, sut.get(1));

    }

    @Test
    public void getShouldReturnSettedStep() throws Exception {
        sut.set(1, someStep);
        assertEquals(someStep, sut.get(1));

    }
}