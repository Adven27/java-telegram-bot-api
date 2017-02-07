package com.pengrad.telegrambot.listeners;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.commands.BotCommand;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.*;
import java.util.function.BiConsumer;

import static com.pengrad.telegrambot.model.MessageEntity.Type.bot_command;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class CommandAwareListener implements UpdatesListener {
    private final TelegramBot bot;
    private final Map<String, BotCommand> commandRegistry = new HashMap<>();
    private BiConsumer<TelegramBot, Message> defaultConsumer;

    public CommandAwareListener(TelegramBot bot, BotCommand... commands) {
        this.bot = bot;
        registerAll(commands);
    }

    @Override
    public int process(List<Update> updates) {
        updates.stream().filter(u -> u.message() != null).forEach(u -> processMessage(bot, u.message()));
        return CONFIRMED_UPDATES_ALL;
    }

    private void processMessage(TelegramBot bot, Message msg) {
        List<CommandInvocation> invocations = parseCommandInvocations(msg);
        if (invocations.isEmpty()) {
            notCommandAnswer(bot, msg);
        } else {
            tryExecuteCommands(bot, msg, invocations);
        }
    }

    private void tryExecuteCommands(TelegramBot bot, Message msg, List<CommandInvocation> commandInvocations) {
        for (CommandInvocation inv : commandInvocations) {
            BotCommand cmd = commandRegistry.get(inv.name);
            if (cmd != null) {
                cmd.execute(bot, msg.from(), msg.chat(), inv.params);
            } else if (defaultConsumer != null) {
                defaultConsumer.accept(bot, msg);
            }
        }
    }

    public void registerDefaultAction(BiConsumer<TelegramBot, Message> defaultConsumer) {
        this.defaultConsumer = defaultConsumer;
    }

    public final void register(BotCommand botCommand) {
        String id = botCommand.identifier();
        if (commandRegistry.containsKey(id)) {
            throw new IllegalArgumentException("Command " + id + " already registered.");
        }
        commandRegistry.put(id, botCommand);
    }

    public final void registerAll(BotCommand... commands) {
        for (BotCommand c : commands) {
            register(c);
        }
    }

    public final boolean deregister(BotCommand botCommand) {
        if (commandRegistry.containsKey(botCommand.identifier())) {
            commandRegistry.remove(botCommand.identifier());
            return true;
        }
        return false;
    }

    public final Map<BotCommand, Boolean> deregisterAll(BotCommand... commands) {
        Map<BotCommand, Boolean> resultMap = new HashMap<>(commands.length);
        for (BotCommand c : commands) {
            resultMap.put(c, deregister(c));
        }
        return resultMap;
    }

    private List<CommandInvocation> parseCommandInvocations(Message msg) {
        return  msg.entities() == null ? emptyList() : stream(msg.entities()).
                filter(e -> e.type().equals(bot_command)).
                sorted((e1, e2) -> e1.offset().compareTo(e2.offset())).
                map(e -> new CommandInvocation(msg.text().substring(e.offset(), e.length()),
                        msg.text().substring(e.offset() + e.length()))).
                collect(toList());
    }

    private void notCommandAnswer(TelegramBot bot, Message msg) {
        bot.execute(new SendMessage(msg.chat().id(), "answer to not command"));
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