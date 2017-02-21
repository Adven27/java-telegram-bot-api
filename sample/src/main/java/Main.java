import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.listeners.HandlersChainListener;
import com.pengrad.telegrambot.listeners.handlers.MessageHandler;
import com.pengrad.telegrambot.listeners.handlers.UpdateHandler;
import net.mamot.bot.commands.*;
import net.mamot.bot.services.impl.AdvicePrinter;
import net.mamot.bot.services.impl.AdviceResource;
import net.mamot.bot.services.impl.MessageFromURL;
import net.mamot.bot.services.impl.UpnpBridgeAdapter;

import static com.pengrad.telegrambot.TelegramBotAdapter.buildDebug;
import static java.util.Arrays.asList;

public class Main {

    public static final String TOKEN = "310779376:AAEnyT1RnnTHn4UFlql7Ib-8MRF5z_sAtyU";

    public static void main(String[] args) {
        TelegramBot bot = buildDebug(TOKEN);

        LightsCommand lightsCommand = new LightsCommand(new UpnpBridgeAdapter());
        TicTacToeCommand ticTacToeCommand = new TicTacToeCommand();
        InlineTestCommand inlineTestCommand = new InlineTestCommand();
        UpdateHandler messageHandler = new MessageHandler(
                new HelloCommand(),
                inlineTestCommand,
                lightsCommand,
                ticTacToeCommand,
                new AdviceCommand(new MessageFromURL(new AdviceResource(), new AdvicePrinter())));

        bot.setUpdatesListener(new HandlersChainListener(bot, asList(
                messageHandler, lightsCommand, inlineTestCommand,ticTacToeCommand
        )));
    }

}