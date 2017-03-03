package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import net.mamot.bot.services.twitter.TwitterService;

import static com.pengrad.telegrambot.request.SendMessage.message;

//TODO tests
public class TwitterGirlCommand extends MessageCommand {
    private final TwitterService twitter;
    private static final String GIRL_NAME_IN_TWITTER = "besseifunction";

    public TwitterGirlCommand(TwitterService twitter) {
        super("/scalagirl", "Latest tweet of scala girl");
        this.twitter = twitter;
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        bot.execute(message(chat, twitter.getLatestTweet(GIRL_NAME_IN_TWITTER)).disableWebPagePreview(false));
    }
}
