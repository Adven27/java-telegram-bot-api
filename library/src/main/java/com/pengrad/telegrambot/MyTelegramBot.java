package com.pengrad.telegrambot;

import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.List;


public class MyTelegramBot {
    private final TelegramBot bot;
    private final UpdatesListener updatesListener;

    public MyTelegramBot(TelegramBot bot, UpdatesListener updatesListener) {
        this.bot = bot;
        this.updatesListener = updatesListener;
    }

    public MyTelegramBot(final TelegramBot bot) {
        this.bot = bot;
        this.updatesListener = new UpdatesListener() {
            @Override
            public int process(List<Update> updates) {
                for (Update upd : updates) {
                    if (upd.message() != null) {
                        if (upd.message().entities() != null && upd.message().entities().length > 0) {
                            for (MessageEntity messageEntity : upd.message().entities()) {
                                if (messageEntity.type().equals(MessageEntity.Type.bot_command)) {
                                    //TODO commands handling
                                    bot.execute(new SendMessage(upd.message().chat().id(), "answer for command"));
                                }
                            }
                        }
                    }
                    bot.execute(new SendMessage(upd.message().chat().id(), "???"));
                }
                return CONFIRMED_UPDATES_ALL;
            }
        };
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
