package com.pengrad.telegrambot.commands;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.listeners.handlers.CallbackHandler;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

public abstract class CallbackCommand extends MessageCommand implements CallbackHandler {

    public CallbackCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    public boolean handle(TelegramBot bot, Update update) {
        TelegramBot signCallbackBot = new SignCallbackDecoratorBot(bot);
        CallbackQuery cb = update.callbackQuery();
        if (cb == null) {
            return super.handle(signCallbackBot, update);
        }
        return isThisCommandCallback(cb) ? callback(signCallbackBot, cb) : false;
    }

    private boolean isThisCommandCallback(CallbackQuery cb) {
        String data = cb.data();
        String prefix = prefix();
        if(data.startsWith(prefix)) {
            cb.setData(data.substring(prefix.length()));
            return true;
        }
        return false;
    }

    public String signCallback(String data) {
        return prefix() + data;
    }

    public  <T extends BaseRequest, R extends BaseResponse> BaseRequest<T, R> signRequest(BaseRequest<T, R> request) {
        if (request instanceof SendMessage) {
            SendMessage message = (SendMessage) request;
            Keyboard replyMarkup = (Keyboard) message.getParameters().get("reply_markup");
            if(replyMarkup instanceof InlineKeyboardMarkup) {
                InlineKeyboardMarkup inlineKeyboard = (InlineKeyboardMarkup) replyMarkup;
                message.replyMarkup(signCallbackKeyboard(inlineKeyboard));
                request = (BaseRequest<T, R>) message;
            }
        } else if (request instanceof EditMessageText) {
            EditMessageText message = (EditMessageText) request;
            Keyboard replyMarkup = (Keyboard) message.getParameters().get("reply_markup");
            if(replyMarkup instanceof InlineKeyboardMarkup) {
                InlineKeyboardMarkup inlineKeyboard = (InlineKeyboardMarkup) replyMarkup;
                message.replyMarkup(signCallbackKeyboard(inlineKeyboard));
                request = (BaseRequest<T, R>) message;
            }
        }
        return request;
    }

    private final String prefix() {
        return identifier() + ":";
    }

    private InlineKeyboardMarkup signCallbackKeyboard(InlineKeyboardMarkup markup) {
        for (InlineKeyboardButton[] row : markup.keyboard()) {
            for (InlineKeyboardButton button : row) {
                button.callbackData(signCallback(button.callbackData()));
            }
        }
        return markup;
    }

    class SignCallbackDecoratorBot implements TelegramBot {
        private final TelegramBot original;

        public SignCallbackDecoratorBot(TelegramBot original) {
            this.original = original;
        }

        @Override
        public <T extends BaseRequest, R extends BaseResponse> R execute(BaseRequest<T, R> request) {
            return original.execute(signRequest(request));
        }

        @Override
        public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(T request, Callback<T, R> callback) {
            original.execute(request, callback);
        }

        @Override
        public void setUpdatesListener(UpdatesListener listener) {
            original.setUpdatesListener(listener);
        }

        @Override
        public void setUpdatesListener(UpdatesListener listener, GetUpdates request) {
            original.setUpdatesListener(listener, request);
        }

        @Override
        public void removeGetUpdatesListener() {
            original.removeGetUpdatesListener();
        }
    }
}