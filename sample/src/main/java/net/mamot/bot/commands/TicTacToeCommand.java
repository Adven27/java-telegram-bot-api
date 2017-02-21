package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.CallbackCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import net.mamot.bot.games.TicTacToe;

import static com.pengrad.telegrambot.model.request.InlineKeyboardMarkup.*;
import static com.pengrad.telegrambot.request.SendMessage.message;
import static java.lang.Integer.parseInt;

public class TicTacToeCommand extends CallbackCommand {
    private final TicTacToe game = new TicTacToe();
    private Integer originalMessage;

    public TicTacToeCommand() {
        super("/ttt", "Try to beat me skin bastard...");
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        SendResponse response = bot.execute(message(chat, game.toString()).replyMarkup(getKeyboard()));
        originalMessage = response.message().messageId();
    }

    @Override
    public Integer originalMessage() {
        return originalMessage;
    }

    @Override
    public boolean callback(TelegramBot bot, CallbackQuery cb) {
        if ("r".equals(cb.data())) {
            game.reset();
        } else {
            game.move(parseInt(cb.data()));
        }
        bot.execute(new EditMessageText(cb.message().chat().id(), originalMessage, game.toString()).replyMarkup(getKeyboard()));
        return true;
    }

    private InlineKeyboardMarkup getKeyboard() {
        return keyboard(
                row(btn("0", "0"),btn("1", "1"),btn("2", "2")),
                row(btn("3", "3"),btn("4", "4"),btn("5", "5")),
                row(btn("6", "6"),btn("7", "7"),btn("8", "8")),
                row(btn("reset", "r"))
        );
    }
}