package net.mamot.bot.services.twitter;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import java.util.Optional;

import static java.util.Optional.empty;

public class TwitterServiceImpl implements TwitterService {
    private static final String NO_TWEETS = "No tweets for you bro :(";
    private Twitter twitter = TwitterFactory.getSingleton();
    private String lastPosted = "";

    @Override
    public String getLatestTweet(String userName) {
        lastPosted = getLatest(userName);
        return lastPosted;
    }

    @Override
    public Optional<String> getLatestNewTweet(String userName) {
        String latest = getLatest(userName);
        if (latest.equals(lastPosted) || latest.equals(NO_TWEETS)) {
            return empty();
        }
        lastPosted = latest;
        return Optional.of(latest);
    }

    private String getLatest(String userName) {
        try {
            QueryResult result = twitter.search(new Query("from:" + userName));
            return result.getTweets().get(0).getText();
        } catch (Exception ex) {
            return NO_TWEETS;
        }
    }
}
