package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.CallbackCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import net.mamot.bot.games.TicTacToe;

import static com.pengrad.telegrambot.fluent.KeyboardBuilder.keyboard;
import static com.pengrad.telegrambot.request.EditMessageText.editMessage;
import static com.pengrad.telegrambot.request.SendMessage.message;
import static java.lang.Integer.parseInt;
import static net.mamot.bot.games.TicTacToe.Result.NONE;

public class TicTacToeCommand extends CallbackCommand {
    public static final String COMMAND = "/ttt";
    private final TicTacToe game = new TicTacToe();

    public TicTacToeCommand() {
        super(COMMAND, "Try to beat me, skin bastard...");
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        bot.execute(message(chat, game.toString()).replyMarkup(getKeyboard()));
    }

    @Override
    public boolean callback(TelegramBot bot, CallbackQuery cb) {
        if ("r".equals(cb.data())) {
            game.reset();
        } else if (game.result() == NONE){
            game.move(parseInt(cb.data()));
        } else {
            bot.execute(new AnswerCallbackQuery(cb.id()).text("Game Over"));
            return true;
        }
        bot.execute(editMessage(cb.message(), game.toString()).replyMarkup(getKeyboard()));
        return true;
    }

    private InlineKeyboardMarkup getKeyboard() {
        return keyboard().row("0", "0", "1", "1", "2", "2").
                          row("3", "3", "4", "4", "5", "5").
                          row("6", "6", "7", "7", "8", "8").
                          row("reset", "r").build();
    }
}