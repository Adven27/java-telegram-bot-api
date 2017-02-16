package net.mamot.bot.services.impl;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class HueLightsServiceTest {

    private BridgeFinder bridgeFinder = mock(BridgeFinder.class);
    HueLightsService sut = new HueLightsService(bridgeFinder);

    @Test
    public void turnOffAll() throws Exception {
        sut.turnOffAll();
    }
}