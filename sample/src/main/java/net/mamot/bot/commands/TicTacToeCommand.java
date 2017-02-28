package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.CallbackCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import net.mamot.bot.games.TicTacToe;

import static com.pengrad.telegrambot.fluent.KeyboardBuilder.keyboard;
import static com.pengrad.telegrambot.request.EditMessageText.editMessage;
import static com.pengrad.telegrambot.request.SendMessage.message;
import static java.lang.Integer.parseInt;

public class TicTacToeCommand extends CallbackCommand {
    private final TicTacToe game = new TicTacToe();

    public TicTacToeCommand() {
        super("/ttt", "Try to beat me, skin bastard...");
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        bot.execute(message(chat, game.toString()).replyMarkup(getKeyboard()));
    }

    @Override
    public boolean callback(TelegramBot bot, CallbackQuery cb) {
        if ("r".equals(cb.data())) {
            game.reset();
        } else {
            game.move(parseInt(cb.data()));
        }
        bot.execute(editMessage(cb.message(), game.toString()).replyMarkup(getKeyboard()));
        return true;
    }

    private InlineKeyboardMarkup getKeyboard() {
        return keyboard().row(
                "0", "0", "1", "1", "2", "2",
                "3", "3", "4", "4", "5", "5",
                "6", "6", "7", "7", "8", "8",
                "reset", "r").build();
    }
}