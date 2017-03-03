package net.mamot.bot.services.twitter;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class TwitterServiceImpl implements TwitterService {
    Twitter twitter = TwitterFactory.getSingleton();

    @Override
    public String getLatestTweet(String userName) {
        try {
            QueryResult result = twitter.search(new Query("from:" + userName));
            return result.getTweets().get(0).getText();
        } catch (Exception ex) {
            return "No twits for you bro :(";
        }
    }
}
