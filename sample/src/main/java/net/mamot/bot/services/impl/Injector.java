package net.mamot.bot.services.impl;

import net.mamot.bot.services.debts.DebtsRepo;
import net.mamot.bot.services.debts.Favorites;
import net.mamot.bot.services.twitter.TwitterService;
import net.mamot.bot.services.twitter.TwitterServiceImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Injector {
    private static Map<String, Object> map = new ConcurrentHashMap<String, Object>();
    static {
        bind(Favorites.class, new Favorites());
        bind(DebtsRepo.class, new DebtsRepo());
        bind(TwitterService.class, new TwitterServiceImpl());
    }

    public static Object provide(Class c) {
        return map.get(c.getName());
    }

    public static void bind(Class c, Object impl) {
        map.put(c.getName(), impl);
    }
}
