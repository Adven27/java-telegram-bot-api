package com.pengrad.telegrambot;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.List;

import static com.pengrad.telegrambot.UpdatesListener.CONFIRMED_UPDATES_ALL;
import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;


public class MyTelegramBot {
    private final TelegramBot bot;
    private final UpdatesListener updatesListener;

    public MyTelegramBot(TelegramBot bot, UpdatesListener updatesListener) {
        this.bot = bot;
        this.updatesListener = updatesListener;
    }

    public MyTelegramBot(final TelegramBot bot) {
        this.bot = bot;
        this.updatesListener = updates -> {
            for (Update upd : updates) {
                if (upd.message() != null) {
                    if (!processMessage(bot, upd.message())) {
                        notCommandAnswer(bot, upd.message());
                    }
                }
            }
            return CONFIRMED_UPDATES_ALL;
        };
    }

    private boolean processMessage(TelegramBot bot, Message msg) {
        boolean res = false;
        if (hasEntities(msg)) {
            for (MessageEntity ent : msg.entities()) {
                if (isCommand(ent)) {
                    //TODO commands handling
                    commandAnswer(bot, msg);
                    res = true;
                }
            }
        }
        return res;
    }

    private void commandAnswer(TelegramBot bot, Message msg) {
        bot.execute(new SendMessage(msg.chat().id(), "answer for command"));
    }

    private void notCommandAnswer(TelegramBot bot, Message msg) {
        bot.execute(new SendMessage(msg.chat().id(), "answer to not command"));
    }

    private boolean isCommand(MessageEntity messageEntity) {
        return messageEntity.type().equals(bot_command);
    }

    private boolean hasEntities(Message msg) {
        return msg.entities() != null && msg.entities().length > 0;
    }

    public void start() {
        bot.setUpdatesListener(updatesListener);
    }

    public void doOnce() {
        bot.setUpdatesListener(new OneTimeListener(bot, updatesListener));
    }

    public void stop() {
        bot.removeGetUpdatesListener();
    }

    class OneTimeListener implements UpdatesListener {

        private final TelegramBot bot;
        private final UpdatesListener original;

        OneTimeListener(TelegramBot bot, UpdatesListener original) {
            this.bot = bot;
            this.original = original;
        }

        @Override
        public int process(List<Update> updates) {
            int res = original.process(updates);
            bot.removeGetUpdatesListener();
            return res;
        }
    }
}
