import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.listeners.HandlersChainListener;
import net.mamot.bot.commands.*;
import net.mamot.bot.services.impl.DAO;
import net.mamot.bot.services.LocalizationService;
import net.mamot.bot.services.advice.impl.AdvicePrinter;
import net.mamot.bot.services.advice.impl.AdviceResource;
import net.mamot.bot.services.lights.impl.UpnpBridgeAdapter;
import net.mamot.bot.services.weather.Weather;
import net.mamot.bot.services.games.impl.LeaderBoardImpl;
import net.mamot.bot.services.games.impl.PGSQLGameLeaderBoardRepo;
import net.mamot.bot.services.games.impl.PGSQLGameRepo;
import net.mamot.bot.services.impl.*;
import net.mamot.bot.services.weather.impl.SimpleWeather;
import net.mamot.bot.services.weather.impl.WeatherLoggingDecorator;
import net.mamot.bot.services.weather.impl.WeatherPrinter;
import net.mamot.bot.services.weather.impl.WeatherResource;

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
                new WeatherCommand(weather),
                new Game2048Command(new PGSQLGameRepo(), new LeaderBoardImpl(new PGSQLGameLeaderBoardRepo()))
        ));
    }

}