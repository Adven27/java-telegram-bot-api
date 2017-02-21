import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.listeners.HandlersChainListener;
import net.mamot.bot.commands.*;
import net.mamot.bot.services.impl.AdvicePrinter;
import net.mamot.bot.services.impl.AdviceResource;
import net.mamot.bot.services.impl.MessageFromURL;
import net.mamot.bot.services.impl.UpnpBridgeAdapter;

import static com.pengrad.telegrambot.TelegramBotAdapter.buildDebug;
import static com.pengrad.telegrambot.tester.BotTester.message;

public class Main {

    public static final String TOKEN = "310779376:AAEnyT1RnnTHn4UFlql7Ib-8MRF5z_sAtyU";

    public static void main(String[] args) {
        TelegramBot bot = buildDebug(TOKEN);

        bot.setUpdatesListener(new HandlersChainListener(bot, (b, u) -> b.execute(message("help")) != null,
                new HelloCommand(),
                new AdviceCommand(new MessageFromURL(new AdviceResource(), new AdvicePrinter())),
                new LightsCommand(new UpnpBridgeAdapter()),
                new InlineTestCommand(),
                new TicTacToeCommand()
        ));
    }

}