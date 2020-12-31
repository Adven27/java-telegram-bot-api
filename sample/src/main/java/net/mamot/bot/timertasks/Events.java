package net.mamot.bot.timertasks;

import net.mamot.bot.services.Stickers;

import java.time.LocalTime;

import static net.mamot.bot.services.Stickers.*;

public enum Events {
    MORNING("На работу!!!", RUN, LocalTime.parse("09:00:00"));

    private final String msg;
    private final Stickers sticker;
    private final LocalTime time;

    Events(String msg, Stickers sticker, LocalTime time) {
        this.msg = msg;
        this.sticker = sticker;
        this.time = time;
    }

    public String msg() {
        return msg;
    }

    public Stickers sticker() {
        return sticker;
    }

    public LocalTime time() {
        return time;
    }
}
