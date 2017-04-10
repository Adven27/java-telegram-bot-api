package net.mamot.bot.timertasks;

import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import net.mamot.bot.services.Stickers;
import net.mamot.bot.services.twitter.TwitterService;

import java.util.Optional;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;

public class TwitterTask extends CustomTimerTask {
    private static final long TWEET_POLLING_DELAY = 1800000L;
    private final TwitterService twitter;
    private final String twitterName;
    private final int subscriber;

    public TwitterTask(TwitterService twitter, String twitterName, int subscriber) {
        super("Twitter polling task: " + twitterName, -1);
        this.twitter = twitter;
        this.twitterName = twitterName;
        this.subscriber = subscriber;
    }

    @Override
    public void execute() {
        Optional<String> latestNew = twitter.getLatestNewTweet(twitterName);
        if (latestNew.isPresent()) {
            bot.execute(new SendSticker(subscriber, Stickers.BLA.id()));
            bot.execute(new SendMessage(subscriber,
                    latestNew.get()).parseMode(HTML).disableWebPagePreview(false));
        }
    }

    @Override
    public long computeDelay() {
        return TWEET_POLLING_DELAY;
    }
}