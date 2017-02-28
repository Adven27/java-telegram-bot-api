import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.listeners.HandlersChainListener;
import net.mamot.bot.commands.*;
import net.mamot.bot.services.DAO;
import net.mamot.bot.services.LocalizationService;
import net.mamot.bot.services.Weather;
import net.mamot.bot.services.impl.*;

import static com.pengrad.telegrambot.TelegramBotAdapter.buildDebug;
import static com.pengrad.telegrambot.tester.BotTester.message;

public class Main {

    public static final String TOKEN = "310779376:AAEnyT1RnnTHn4UFlql7Ib-8MRF5z_sAtyU";

    public static void main(String[] args) {
        TelegramBot bot = buildDebug(TOKEN);

        final LocalizationService localizationService = new LocalizationService();
        final DAO dao = new DAO();
        WeatherPrinter weatherPrinter = new WeatherPrinter(localizationService, dao);
        WeatherResource weatherResource = new WeatherResource();
        Weather weather = new WeatherLoggingDecorator(
                new SimpleWeather(weatherPrinter, weatherResource), localizationService);

        bot.setUpdatesListener(new HandlersChainListener(bot, (b, u) -> b.execute(message("help")) != null,
                new HelloCommand(),
                new AdviceCommand(new MessageFromURL(new AdviceResource(), new AdvicePrinter())),
                new LightsCommand(new UpnpBridgeAdapter()),
                new TicTacToeCommand(),
                new WeatherCommand(weather)
        ));
    }

}