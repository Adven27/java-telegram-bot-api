package com.pengrad.telegrambot.tester;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.BotCommand;
import com.pengrad.telegrambot.listeners.HandlersChainListener;
import com.pengrad.telegrambot.listeners.OneTimeListenerDecorator;
import com.pengrad.telegrambot.listeners.handlers.MessageHandler;
import com.pengrad.telegrambot.listeners.handlers.UpdateHandler;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import ru.lanwen.verbalregex.VerbalExpression;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static ru.lanwen.verbalregex.VerbalExpression.regex;

public class BotTester {

    public static GivenSpec given(BotCommand... botCommands) {
        return new GivenSpec(new ArrayList<>(), botCommands);
    }

    public static GivenSpec given(List<UpdateHandler> handlers, BotCommand... botCommands) {
        return new GivenSpec(handlers, botCommands);
    }

    //TODO rid of hardcoded chat ID
    public static SendMessage message(String text) {
        return new SendMessage(1 , text);
    }

    public static SendSticker sticker(String sticker) {
        return new SendSticker(1 , sticker);
    }

    public static BotCommand command(final String identifier, final Consumer<TelegramBot> consumer) {
        return new BotCommand(identifier, "Hello World") {
            @Override
            public void execute(TelegramBot bot, User user, Chat chat, String params) {
                consumer.accept(bot);
            }
        };
    }

    public static class GivenSpec {
        private String callbackData = "";
        private int date = 1485957820;
        private String text = "";
        private User user = createUser();
        private Chat chat = createChat();

        private final List<UpdateHandler> handlers;
        private final BotCommand[] botCommands;
        private BiConsumer<TelegramBot, Message> defaultConsumer;

        public GivenSpec(List<UpdateHandler> handlers, BotCommand[] botCommands) {
            this.handlers = handlers;
            this.botCommands = botCommands;
        }

        public GivenSpec got(String txt) {
            this.text = txt;
            return this;
        }

        public GivenSpec gotCallback(String callbackData) {
            this.callbackData = callbackData;
            return this;
        }

        public GivenSpec defaultAction(BiConsumer<TelegramBot, Message> defaultConsumer) {
            this.defaultConsumer = defaultConsumer;
            return this;
        }

        public GivenSpec chat(String id, Chat.Type type) {
            //chat =  format(chatTemplate, id, type.name().toLowerCase());
            return this;
        }

        public GivenSpec chat(String id) {
            return chat(id, Chat.Type.Private);
        }

        public GivenSpec from(int id, String firstName) {
            user.setId(id);
            user.setFirst_name(firstName);
            return this;
        }

        public GivenSpec date(Date dt) {
            //date = dt.getTime();
            return this;
        }

        public ThenSpec then() {
            //TODO refactor
            GetUpdatesResponse updatesResponse = new GetUpdatesResponse();
            Update update = new Update();
            update.setUpdate_id(1);
            if (callbackData.isEmpty()) {
                update.setMessage(createMessage(text, chat, user, date));
            } else {
                CallbackQuery callback_query = new CallbackQuery();
                callback_query.setMessage(createMessage(text, chat, user, date));
                callback_query.setData(callbackData);
                update.setCallback_query(callback_query);
            }
            updatesResponse.setResult(singletonList(update));
            FakeBotApi botApi = new FakeBotApi(updatesResponse);
            TelegramBot bot = new TelegramBot(botApi);
            startOnce(bot);
            return new ThenSpec(botApi.requests());
        }

        private void startOnce(TelegramBot bot) {
            MessageHandler messageHandler = new MessageHandler(botCommands);
            messageHandler.registerDefaultAction(defaultConsumer);
            handlers.add(messageHandler);
            HandlersChainListener listener = new HandlersChainListener(bot, handlers);
            bot.setUpdatesListener(new OneTimeListenerDecorator(bot, listener));
        }
    }

    private static Message createMessage(String text, Chat chat, User user, Integer date) {
        Message message = new Message();
        message.setText(text);
        message.setChat(chat);
        message.setFrom(user);
        message.setDate(date);
        message.setEntities(parseEntities(text).toArray(new MessageEntity[0]));
        return message;
    }

    private static List<MessageEntity> parseEntities(String text) {
        VerbalExpression cmdExp = regex().find("/").oneOrMore().word().build();
        return cmdExp.getTextGroups(text, 0).stream().map(
                s ->  createMessageEntity(s.length(), 0)).collect(Collectors.toList());
    }

    private static MessageEntity createMessageEntity(Integer length, Integer offset) {
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setLength(length);
        messageEntity.setOffset(offset);
        messageEntity.setType(MessageEntity.Type.bot_command);
        return messageEntity;
    }

    private static Chat createChat() {
        Chat chat = new Chat();
        chat.setType(Chat.Type.Private);
        chat.setId(1L);
        return chat;
    }

    private static User createUser() {
        User user = new User();
        user.setId(1);
        user.setFirst_name("Walter");
        user.setLast_name("White");
        user.setUsername("heisenberg");
        return user;
    }

    public static class ThenSpec {
        private final List<BaseRequest> actualRequests;
        private final RequestMatcher matcher = new RequestMatcher();

        public ThenSpec(List<BaseRequest> actualRequests) {
            this.actualRequests = actualRequests;
        }

        public void shouldAnswer(BaseRequest... expectedRequests) {
            List<RequestMatcher.Mismatch> mismatches = matcher.match(expectedRequests, actualRequests);
            if (!mismatches.isEmpty()) {
                throw new AssertionError(mismatches);
            }
        }
    }
}