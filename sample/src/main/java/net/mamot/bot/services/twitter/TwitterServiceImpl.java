package net.mamot.bot.services.twitter;

import twitter4j.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;

public class TwitterServiceImpl implements TwitterService {
    private static final String NO_TWEETS = "No tweets for you bro :(";
    private Twitter twitter = TwitterFactory.getSingleton();
    private Map<String, String> lastPosted = new HashMap<>();

    @Override
    public String getLatestTweet(String userName) {
        String latest = getLatest(userName);
        lastPosted.put(userName, latest);
        return latest;
    }

    @Override
    public Optional<String> getLatestNewTweet(String userName) {
        String latest = getLatest(userName);
        if (latest.equals(lastPosted.get(userName)) || latest.equals(NO_TWEETS)) {
            return empty();
        }
        lastPosted.put(userName, latest);
        return Optional.of(latest);
    }

    private String getLatest(String userName) {
        try {
            QueryResult result = twitter.search(new Query("from:" + userName));
            Status tweet = result.getTweets().get(0);
            return tweet.getText() + "\n" + tweet.getUser().getURL();
        } catch (Exception ex) {
            return NO_TWEETS;
        }
    }
}
