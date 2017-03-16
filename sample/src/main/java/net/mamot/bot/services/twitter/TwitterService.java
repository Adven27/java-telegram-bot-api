package net.mamot.bot.services.twitter;

import java.util.Optional;

public interface TwitterService {
    String getLatestTweet(String userName);
    Optional<String> getLatestNewTweet(String userName);
}
