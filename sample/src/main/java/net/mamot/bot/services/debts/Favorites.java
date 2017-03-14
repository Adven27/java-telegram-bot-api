package net.mamot.bot.services.debts;

import com.google.common.collect.Maps;

import java.util.Map;

public class Favorites {
    private final Map<String, String> map = Maps.newHashMap();

    public Favorites() {
        map.put("Мамай", "Мамай");
        map.put("Волтер Вайт", "Волтер Вайт");
        map.put("Аль Капоне", "Аль Капоне");
    }

    public Map<String, String> getAll(){
        return map;
    }

    public void add(String data) {
        map.put(data, data);
    }
}