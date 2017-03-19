package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.logging.BotLogger;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.response.SendResponse;
import net.mamot.bot.feed.Entry;
import net.mamot.bot.feed.Feed;
import net.mamot.bot.feed.atom.AtomFeed;
import net.mamot.bot.feed.printer.EntryPrinter;

import static com.pengrad.telegrambot.model.request.ParseMode.HTML;
import static com.pengrad.telegrambot.request.SendMessage.message;
import static com.pengrad.telegrambot.request.SendSticker.sticker;
import static net.mamot.bot.services.Stickers.HELP;
import static net.mamot.bot.services.Stickers.THINK;

public class UncleBobCommand extends MessageCommand {

    public static final String COMMAND = "/unclebob";

    static final String COMMAND_FORMAT_ERROR_MESSAGE = "Number of requested articles should be positive.";

    private static final String FEED_PATH = "http://blog.cleancoder.com/atom.xml";

    private final EntryPrinter printer;

    public UncleBobCommand(EntryPrinter printer) {
        super(COMMAND, "Latest articles of Uncle Bob blog");
        this.printer = printer;
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        Feed feed = new AtomFeed(FEED_PATH);
        if (!params.isEmpty()) {
            try {
                int number = Integer.parseInt(params.trim());
                if (number > 0) {
                    sendNumberOfLatest(bot, chat, feed, number);
                } else {
                    sendFormatError(bot, chat);
                }
            } catch (NumberFormatException e) {
                sendFormatError(bot, chat);
            }
        } else {
            sendLatest(bot, chat, feed);
        }
    }

    private void sendNumberOfLatest(TelegramBot bot, Chat chat, Feed feed, int number) {
        bot.execute(sticker(chat, THINK.id()));
        feed.get(number).forEach(e -> sendArticle(bot, chat, e));
    }

    private SendResponse sendArticle(TelegramBot bot, Chat chat, Entry e) {
        return bot.execute(message(chat, printer.print(e)).parseMode(HTML).disableWebPagePreview(false));
    }

    private void sendLatest(TelegramBot bot, Chat chat, Feed feed) {
        bot.execute(sticker(chat, THINK.id()));
        sendArticle(bot, chat, feed.get());
    }

    private void sendFormatError(TelegramBot bot, Chat chat) {
        BotLogger.info("UncleBobCommand", COMMAND_FORMAT_ERROR_MESSAGE);
        bot.execute(sticker(chat, HELP.id()));
        bot.execute(message(chat, COMMAND_FORMAT_ERROR_MESSAGE).parseMode(HTML).disableWebPagePreview(false));
    }

}
