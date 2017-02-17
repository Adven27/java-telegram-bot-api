package net.mamot.bot.services.impl;

import net.mamot.bot.services.HueBridge;

public class FakeHueBridge implements HueBridge {
    private final String id;
    private final String desc;
    private final String url;

    public FakeHueBridge(String id, String desc, String url) {
        this.id = id;
        this.desc = desc;
        this.url = url;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public void turnOn(String id) {}

    @Override
    public void turnOff(String id) {}

    @Override
    public void turnOnAll() {}

    @Override
    public void turnOffAll() {}
}