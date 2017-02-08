package com.pengrad.telegrambot.tester;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.BotCommand;
import com.pengrad.telegrambot.listeners.HandlersChainListener;
import com.pengrad.telegrambot.listeners.OneTimeListenerDecorator;
import com.pengrad.telegrambot.listeners.handlers.MessageHandler;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import ru.lanwen.verbalregex.VerbalExpression;

import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static ru.lanwen.verbalregex.VerbalExpression.regex;

public class BotTester {

    public static GivenSpec given(BotCommand... botCommands) {
        return new GivenSpec(botCommands);
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
        private final String messageEntityTemplate = "{\"type\":\"bot_command\",\"offset\":%s,\"length\":%s}";
        private final String userTemplate = "{\"id\": %d,\"first_name\": \"%s\",\"last_name\": \"%s\",\"username\": \"%s\"}";
        private final String chatTemplate = "{\"id\": %d,\"type\": \"%s\"}";
        private final String updateTemplate = "{" +
                "    \"ok\":true,\"result\":" +
                "    [{" +
                "        \"update_id\": 563614790,\"message\": {" +
                "            \"message_id\": 373," +
                "            \"from\": %s,\"chat\": %s,\"date\": %s," +
                "            \"text\": \"%s\" %s"+
                "        }" +
                "    }]" +
                "}";
        private long date = 1485957820;
        private String text = "";
        private String user = "{\"id\": 1,\"first_name\": \"White\",\"last_name\": \"Walter\",\"username\": \"heisenberg\"}";
        private String chat = "{\"id\": 1,\"type\": \"private\"}";

        private final BotCommand[] botCommands;
        private BiConsumer<TelegramBot, Message> defaultConsumer;

        public GivenSpec(BotCommand[] botCommands) {
            this.botCommands = botCommands;
        }

        public GivenSpec got(String txt) {
            this.text = txt;
            return this;
        }

        public GivenSpec defaultAction(BiConsumer<TelegramBot, Message> defaultConsumer) {
            this.defaultConsumer = defaultConsumer;
            return this;
        }

        private String entities(String text) {
            VerbalExpression cmdExp = regex().find("/").oneOrMore().word().build();
            String messageEnt = cmdExp.getTextGroups(text, 0).stream().map(
                        s -> format(messageEntityTemplate, 0, s.length())).collect(joining(","));
            return messageEnt.isEmpty() ? "" : ",\"entities\":[" + messageEnt + "]";
        }

        public GivenSpec chat(String id, Chat.Type type) {
            chat =  format(chatTemplate, id, type.name().toLowerCase());
            return this;
        }

        public GivenSpec chat(String id) {
            return chat(id, Chat.Type.Private);
        }

        public GivenSpec from(String id, String firstName) {
            user = format(userTemplate, id, firstName, "", "");
            return this;
        }

        public GivenSpec date(Date dt) {
            date = dt.getTime();
            return this;
        }

        public ThenSpec then() {
            FakeBotApi botApi = new FakeBotApi(format(updateTemplate, user, chat, date, text, entities(text)));
            TelegramBot bot = new TelegramBot(botApi);
            startOnce(bot);
            return new ThenSpec(botApi.requests());
        }

        private void startOnce(TelegramBot bot) {
            MessageHandler messageHandler = new MessageHandler(botCommands);
            messageHandler.registerDefaultAction(defaultConsumer);
            HandlersChainListener listener = new HandlersChainListener(bot, asList(messageHandler));
            bot.setUpdatesListener(new OneTimeListenerDecorator(bot, listener));
        }
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