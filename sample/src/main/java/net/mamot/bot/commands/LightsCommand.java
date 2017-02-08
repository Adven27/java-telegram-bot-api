package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.BotCommand;
import com.pengrad.telegrambot.listeners.handlers.UpdateHandler;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import net.mamot.bot.services.LightsService;

public class LightsCommand extends BotCommand implements UpdateHandler {

    public static final String CALLBACK_OFF = "off";
    public static final String CALLBACK_ON = "on";
    private final LightsService lightsService;
    private Integer messageId;
    private InlineKeyboardMarkup inlineKeyboard;

    public LightsCommand(LightsService lightsService) {
        super("/lights", "Lights");
        this.lightsService = lightsService;
        inlineKeyboard = new InlineKeyboardMarkup(
                                new InlineKeyboardButton[]{
                                        new InlineKeyboardButton("off").callbackData(CALLBACK_OFF),
                                        new InlineKeyboardButton("on").callbackData(CALLBACK_ON),
                                });
    }

    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        SendResponse response = bot.execute(new SendMessage(chat.id(), "Что сделать?").replyMarkup(inlineKeyboard));
        messageId = response.message().messageId();
    }

    public boolean handle(TelegramBot bot, Update update) {
        CallbackQuery cb = update.callbackQuery();
        return isThisCommandCallback(cb) ? handle(bot, cb) : false;
    }

    private boolean isThisCommandCallback(CallbackQuery cb) {
        return cb != null && cb.message().messageId().equals(messageId);
    }

    private boolean handle(TelegramBot bot, CallbackQuery cb) {
        try {
            processCallback(cb);
            bot.execute(new AnswerCallbackQuery(cb.id()).text("Готово"));
        } catch (LightsService.BridgeUnreachableEx e) {
            apologize(bot, cb.message());
        }
        return true;
    }

    private void apologize(TelegramBot bot, Message msg) {
        bot.execute(new EditMessageText(msg.chat().id(), msg.messageId(), "Не шмагля...").replyMarkup(inlineKeyboard));
    }

    private void processCallback(CallbackQuery cb) {
        switch (cb.data()) {
            case CALLBACK_OFF: lightsService.turnOffAll(); break;
            case CALLBACK_ON: lightsService.turnOnAll(); break;
        }
    }
}