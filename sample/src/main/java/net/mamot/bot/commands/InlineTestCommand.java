package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.CallbackCommand;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import static com.pengrad.telegrambot.model.request.InlineKeyboardMarkup.*;

public class InlineTestCommand extends CallbackCommand{

    public static final String CALLBACK_OFF = "off";
    public static final String CALLBACK_ON = "on";
    private Integer messageId;
    private InlineKeyboardMarkup inlineKeyboard;

    public InlineTestCommand() {
        super("/it", "inline test");
        inlineKeyboard = keyboard(
                row(btn("off", CALLBACK_OFF), btn("on", CALLBACK_ON))
        );
    }

    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        SendMessage request = new SendMessage(chat.id(), "Test").replyMarkup(inlineKeyboard);
        SendResponse response = bot.execute(request);
        messageId = response.message().messageId();
    }

    private void processCallback(CallbackQuery cb) {
        System.out.println("cb = [" + cb + "]");
    }

    @Override
    public boolean callback(TelegramBot bot, CallbackQuery cb) {
        processCallback(cb);
        bot.execute(new AnswerCallbackQuery(cb.id()).text("Готово"));
        return true;
    }

    @Override
    public Integer originalMessage() {
        return null;
    }
}