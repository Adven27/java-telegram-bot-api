package net.mamot.bot.services.games;

import java.util.Map;

public interface GameRepo {
    void insert(String name, String game);
    Map<String, String> selectAll();
    void update(String user, String game);
    void delete(String user);
}
