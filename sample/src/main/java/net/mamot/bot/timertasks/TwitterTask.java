package net.mamot.bot.timertasks;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import net.mamot.bot.services.Stickers;
import net.mamot.bot.services.twitter.TwitterService;
import net.mamot.bot.timertasks.Task.BotTask;

import java.util.Optional;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;

public class TwitterTask extends BotTask {

    private final TwitterService twitter;
    private final String twitterName;
    private final long subscriber;

    public TwitterTask(TwitterService twitter, String twitterName, long subscriber, TelegramBot bot) {
        super(bot);
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
    public String getName() {
        return "Twitter polling task: " + twitterName;
    }
}