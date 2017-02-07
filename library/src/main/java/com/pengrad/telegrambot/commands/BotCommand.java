package com.pengrad.telegrambot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

public abstract class BotCommand {
    private final String commandIdentifier;
    private final String description;

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public BotCommand(String commandIdentifier, String description) {
        this.commandIdentifier = commandIdentifier.toLowerCase();
        this.description = description;
    }

    public final String identifier() {
        return commandIdentifier;
    }

    public final String description() {
        return description;
    }

    /**
     * Execute the command
     *
     * @param bot       bot to send messages over
     * @param user      the user who sent the command
     * @param chat      the chat, to be able to send replies
     * @param params    passed params
     */
    public abstract void execute(TelegramBot bot, User user, Chat chat, String params);
}