package com.pengrad.telegrambot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.listeners.handlers.MessageHandler;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;

import java.util.List;

import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public abstract class MessageCommand implements MessageHandler{
    private final String commandIdentifier;
    private final String desc;

    public MessageCommand(String commandIdentifier, String desc) {
        this.commandIdentifier = commandIdentifier.toLowerCase();
        this.desc = desc;
    }

    public final String identifier() {
        return commandIdentifier;
    }

    public final String description() {
        return desc;
    }

    @Override
    public boolean handle(TelegramBot bot, Update update) {
        Message msg = update.message();
        return msg != null && handle(bot, msg);
    }

    private boolean handle(TelegramBot bot, Message msg) {
        List<CommandInvocation> invocations = parseCommandInvocations(msg);
        return !invocations.isEmpty() && tryExecuteCommands(bot, msg, invocations);
    }

    private boolean tryExecuteCommands(TelegramBot bot, Message msg, List<CommandInvocation> commandInvocations) {
        for (CommandInvocation inv : commandInvocations) {
            if (commandIdentifier.equals(inv.name)) {
                execute(bot, msg.from(), msg.chat(), inv.params);
                return true;
            }
        }
        return false;
    }

    private List<CommandInvocation> parseCommandInvocations(Message msg) {
        return  msg.entities() == null ? emptyList() : stream(msg.entities()).
                filter(e -> e.type().equals(bot_command)).
                sorted(comparing(MessageEntity::offset)).
                map(e -> new CommandInvocation(msg.text().substring(e.offset(), e.length()),
                        msg.text().substring(e.offset() + e.length()))).
                collect(toList());
    }

    private class CommandInvocation {
        private final String name;
        private final String params;

        CommandInvocation(String name, String params) {
            this.name = name;
            this.params = params;
        }
    }
}