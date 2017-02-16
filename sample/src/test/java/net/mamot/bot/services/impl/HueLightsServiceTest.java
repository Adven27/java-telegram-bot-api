package net.mamot.bot.services.impl;

import org.junit.Test;

public class HueLightsServiceTest {

    HueLightsService sut = new HueLightsService();

    @Test
    public void turnOffAll() throws Exception {
        sut.turnOffAll();
    }
}