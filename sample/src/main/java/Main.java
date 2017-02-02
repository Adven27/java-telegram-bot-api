import com.pengrad.telegrambot.MyTelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;

public class Main {

    public static void main(String[] args) {
        new MyTelegramBot(TelegramBotAdapter.build("token")).start();
    }
}
