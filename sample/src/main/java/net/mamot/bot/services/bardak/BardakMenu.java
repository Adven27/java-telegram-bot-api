package net.mamot.bot.services.bardak;

import net.mamot.bot.services.impl.DAO;

import static java.time.LocalDateTime.now;

public class BardakMenu {
    private final DAO dao;

    public BardakMenu(DAO dao) {
        this.dao = dao;
    }

    public String today() {
        return dao.getWeekMenu().get(now().getDayOfWeek().getValue());
    }
}