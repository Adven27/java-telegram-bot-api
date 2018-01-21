package net.mamot.bot.timertasks;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;

public class EventTask extends Task.BotTask {

    private final Events event;
    private final int chat;

    public EventTask(Events event, int chat, TelegramBot bot) {
        super(bot);
        this.event = event;
        this.chat = chat;
    }

    @Override
    public String getName() {
        return event.name();
    }

    @Override
    public void execute() {
        bot.execute(new SendSticker(chat, event.sticker().id()));
        bot.execute(new SendMessage(chat, event.msg()).disableWebPagePreview(true));
    }
}