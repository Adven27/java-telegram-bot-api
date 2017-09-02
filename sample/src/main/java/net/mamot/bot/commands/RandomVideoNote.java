package net.mamot.bot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendVideoNote;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class RandomVideoNote extends MessageCommand {
    Map<String, String> notes = new HashMap<>();

    public RandomVideoNote() {
        super("/antoha", "What does antoha think of?");
        notes.put("agreed", "DQADAgADjgADIoW4SAKvWY_s45OoAg");
        notes.put("agreed2", "DQADAgADjQADIoW4SKPuC8pOJ_RLAg");
        notes.put("angry", "DQADAgADvAADYp-oSAJbNTyejTLOAg");
        notes.put("ha", "DQADAgADuwADYp-oSHf0FUlDP--PAg");
        notes.put("thumbs up", "DQADAgADjwADIoW4SOKQ8l3zPh2dAg");
        notes.put("rock", "DQADAgADbwADW6pJSfv92TwHKmGgAg");
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        bot.execute(new SendVideoNote(chat.id(), note()));
    }

    private String note() {
        Random generator = new Random();
        Object[] values = notes.values().toArray();
        return (String) values[generator.nextInt(values.length)];
    }
}