import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.AdviceCommand;
import com.pengrad.telegrambot.commands.HelloCommand;
import com.pengrad.telegrambot.commands.LightsCommand;
import com.pengrad.telegrambot.listeners.HandlersChainListener;
import com.pengrad.telegrambot.listeners.handlers.MessageHandler;
import com.pengrad.telegrambot.listeners.handlers.UpdateHandler;
import net.mamot.bot.services.impl.AdvicePrinter;
import net.mamot.bot.services.impl.AdviceResource;
import net.mamot.bot.services.impl.HueLightsService;
import net.mamot.bot.services.impl.MessageFromURL;

import static com.pengrad.telegrambot.TelegramBotAdapter.buildDebug;
import static java.util.Arrays.asList;

public class Main {

    public static final String TOKEN = "token";

    public static void main(String[] args) {
        TelegramBot bot = buildDebug(TOKEN);

        LightsCommand lightsCommand = new LightsCommand(new HueLightsService());
        UpdateHandler messageHandler = new MessageHandler(
                new HelloCommand(),
                lightsCommand,
                new AdviceCommand(new MessageFromURL(new AdviceResource(), new AdvicePrinter())));

        bot.setUpdatesListener(new HandlersChainListener(bot, asList(
                messageHandler, lightsCommand
        )));
    }

}