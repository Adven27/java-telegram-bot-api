package net.mamot.bot.services.twitter;

import twitter4j.*;

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
            Status tweet = result.getTweets().get(0);
            return tweet.getText() + "\n" + tweet.getSource();
        } catch (Exception ex) {
            return NO_TWEETS;
        }
    }
}
