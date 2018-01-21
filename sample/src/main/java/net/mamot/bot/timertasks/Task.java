package net.mamot.bot.timertasks;

import com.pengrad.telegrambot.TelegramBot;

public interface Task {
    void execute();

    String getName();

    abstract class BotTask implements Task {
        protected final TelegramBot bot;

        public BotTask(TelegramBot bot) {
            this.bot = bot;
        }
    }
}