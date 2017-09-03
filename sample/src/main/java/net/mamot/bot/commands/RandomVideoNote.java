package net.mamot.bot.commands;

import com.google.common.io.Resources;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendVideoNote;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.pengrad.telegrambot.request.SendMessage.message;


public class RandomVideoNote extends MessageCommand {
    Map<String, URL> notes = new HashMap<>();

    public RandomVideoNote() {
        super("/antoha", "What does antoha think of?");
        notes.put("agreed", Resources.getResource("video-notes/agree.mp4"));
        notes.put("agreed2", Resources.getResource("video-notes/agree2.mp4"));
        notes.put("angry", Resources.getResource("video-notes/angry.mp4"));
        notes.put("ha", Resources.getResource("video-notes/ha.mp4"));
        notes.put("thumbs up", Resources.getResource("video-notes/tu.mp4"));
        notes.put("rock", Resources.getResource("video-notes/rock.mp4"));
    }

    @Override
    public void execute(TelegramBot bot, User user, Chat chat, String params) {
        SendVideoNote videoNote = new SendVideoNote(chat.id(), note());
        bot.execute(videoNote);
        bot.execute(message(chat,videoNote.toString()));
    }

    private byte[] note() {
        Random generator = new Random();
        Object[] values = notes.values().toArray();
        try {
            return Resources.toByteArray((URL) values[generator.nextInt(values.length)]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}