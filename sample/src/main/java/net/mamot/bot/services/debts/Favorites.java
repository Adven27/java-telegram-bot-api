package net.mamot.bot.services.debts;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

public class Favorites {
    public Map<String, String> favorites(){
        HashMap<String, String> map = Maps.newHashMap();
        map.put("Мамай", "Мамай");
        map.put("Волтер Вайт", "Волтер Вайт");
        map.put("Аль Капоне", "Аль Капоне");
        return map;
    }
}
