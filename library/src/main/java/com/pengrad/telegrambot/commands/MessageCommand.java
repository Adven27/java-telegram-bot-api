package com.pengrad.telegrambot.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.listeners.handlers.MessageHandler;
import com.pengrad.telegrambot.model.*;

import java.util.List;

import static com.pengrad.telegrambot.logging.BotLogger.error;
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

    protected String identifier() {
        return commandIdentifier;
    }

    public final String description() {
        return commandIdentifier + " " + desc;
    }

    @Override
    public boolean handle(TelegramBot bot, Update update) {
        Message msg = update.message();
        return msg != null && handle(bot, msg);
    }

    private boolean handle(TelegramBot bot, Message msg) {
        if (msg.replyToMessage() != null) {
            return tryReply(bot, msg, msg.replyToMessage());
        } else {
            List<CommandInvocation> invocations = parseCommandInvocations(msg);
            return !invocations.isEmpty() && tryExecuteCommands(bot, msg, invocations);
        }
    }

    private boolean tryExecuteCommands(TelegramBot bot, Message msg, List<CommandInvocation> commandInvocations) {
        for (CommandInvocation inv : commandInvocations) {
            if (commandIdentifier.equals(inv.name)) {
                try {
                    execute(bot, msg.from(), msg.chat(), inv.params);
                } catch (Exception e) {
                    error("MessageCommand", e);
                }
                return true;
            }
        }
        return false;
    }

    private boolean tryReply(TelegramBot bot, Message reply, Message original) {
        List<CommandInvocation> invocations = parseCommandInvocations(original);
        for (CommandInvocation inv : invocations) {
            if (commandIdentifier.equals(inv.name)) {
                try {
                    reply(bot, reply.from(), reply.chat(), reply.text(), original);
                } catch (Exception e) {
                    error("MessageCommand", e);
                }
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

    @Override
    public void reply(TelegramBot bot, User user, Chat chat, String params, Message original) {

    }

    private static class CommandInvocation {
        private final String name;
        private final String params;

        CommandInvocation(String name, String params) {
            int botNamePostfix = name.indexOf("@");
            this.name = botNamePostfix > 0 ? name.substring(0, botNamePostfix) : name;
            this.params = params;
        }
    }
}