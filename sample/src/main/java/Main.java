import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.AdviceCommand;
import com.pengrad.telegrambot.commands.HelloCommand;
import com.pengrad.telegrambot.listeners.CommandAwareListener;
import net.mamot.bot.services.impl.AdvicePrinter;
import net.mamot.bot.services.impl.AdviceResource;
import net.mamot.bot.services.impl.MessageFromURL;

import static com.pengrad.telegrambot.TelegramBotAdapter.buildDebug;

public class Main {

    public static final String TOKEN = "token";

    public static void main(String[] args) {
        TelegramBot bot = buildDebug(TOKEN);
        bot.setUpdatesListener(new CommandAwareListener(bot,
                new HelloCommand(),
                new AdviceCommand(new MessageFromURL(new AdviceResource(), new AdvicePrinter()))
        ));
    }

}