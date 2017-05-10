package net.mamot.bot.services;

import java.util.Map;

public interface Repo {
    void insert(String user, String data);
    Map<String, String> selectAll();
    String select(String user);
    void update(String user, String data);
    void delete(String user);
    boolean exists (String user, String data);
}
