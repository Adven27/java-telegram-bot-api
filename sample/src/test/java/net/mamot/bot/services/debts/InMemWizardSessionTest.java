package net.mamot.bot.services.debts;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class InMemWizardSessionTest {
    WizardStep initStep = mock(WizardStep.class);
    WizardStep someStep = new WhoStep();
    WizardSession sut = new InMemWizardSession(initStep);

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