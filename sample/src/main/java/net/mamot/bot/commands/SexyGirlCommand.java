package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import net.mamot.bot.services.twitter.TwitterService;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static com.pengrad.telegrambot.request.SendMessage.message;

public class SexyGirlCommand extends MessageCommand {
    public static final String COMMAND = "/sexy";
    private final TwitterService twitter;
    private static final String TWITTER = "dailysocks_";

    public SexyGirlCommand(TwitterService twitter) {
        super(COMMAND, "sexy girls");
        this.twitter = twitter;
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        bot.execute(message(chat, twitter.getLatestTweet(TWITTER)).parseMode(HTML).disableWebPagePreview(false));
    }
}
