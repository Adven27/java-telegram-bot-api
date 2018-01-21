package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import net.mamot.bot.timertasks.Scheduler;


public class DieCommand extends MessageCommand {

    public DieCommand() {
        super("die","R.I.P. mamot");
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        Scheduler.getInstance().stop();
        System.exit(0);
    }
}