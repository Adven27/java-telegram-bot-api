package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import net.mamot.bot.services.twitter.TwitterService;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static com.pengrad.telegrambot.request.SendMessage.message;

public class TwitterGirlCommand extends MessageCommand {
    public static final String COMMAND = "/scalagirl";
    private final TwitterService twitter;
    public static final String GIRL_NAME_IN_TWITTER = "besseifunction";

    public TwitterGirlCommand(TwitterService twitter) {
        super(COMMAND, "Latest tweet of scala girl");
        this.twitter = twitter;
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        bot.execute(message(chat, twitter.getLatestTweet(GIRL_NAME_IN_TWITTER)).parseMode(HTML).disableWebPagePreview(false));
    }
}
