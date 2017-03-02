package com.pengrad.telegrambot.tester;

import com.pengrad.telegrambot.SimpleBot;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.commands.CallbackCommand;
import com.pengrad.telegrambot.commands.MessageCommand;
import com.pengrad.telegrambot.listeners.HandlersChainListener;
import com.pengrad.telegrambot.listeners.OneTimeListenerDecorator;
import com.pengrad.telegrambot.listeners.handlers.UpdateHandler;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import ru.lanwen.verbalregex.VerbalExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static ru.lanwen.verbalregex.VerbalExpression.regex;

public class BotTester {

    public static final int ORIGINAL_MSG_ID = 1;

    public static GivenSpec given(UpdateHandler... handlers) {
        return new GivenSpec(handlers);
    }

    public static GivenSpec givenGotCallbackFor(CallbackCommand cmd, String callbackData) {
        return given(cmd).gotCallbackFor(cmd, callbackData);
    }

    //TODO get rid of hardcoded chat ID
    public static SendMessage message(String text) {
        return new SendMessage(1 , text);
    }

    //TODO get rid of hardcoded IDs
    public static EditMessageText editMessage(String text) {
        return new EditMessageText(1, 1, text);
    }

    public static AnswerCallbackQuery answerCallbackQuery(String text) {
        return new AnswerCallbackQuery(null).text(text);
    }

    public static SendSticker sticker(String sticker) {
        return new SendSticker(1 , sticker);
    }

    public static MessageCommand command(final String identifier, final Consumer<TelegramBot> consumer) {
        return new MessageCommand(identifier, "Hello World") {
            @Override
            public void execute(TelegramBot bot, User user, Chat chat, String params) {
                consumer.accept(bot);
            }
        };
    }

    public static class GivenSpec {
        private String callbackData = "";
        private int originalMsg;
        private int date = 1485957820;
        private String text = "";
        private User user = createUser();
        private Chat chat = createChat();

        private final UpdateHandler[] handlers;
        private UpdateHandler defaultConsumer;

        GivenSpec(UpdateHandler... handlers) {
            this.handlers = handlers;
        }

        public GivenSpec got(String txt) {
            this.text = txt;
            return this;
        }

        public GivenSpec gotCallback(String callbackData) {
            this.callbackData = callbackData;
            this.originalMsg = ORIGINAL_MSG_ID;
            return this;
        }

        public GivenSpec gotCallbackFor(CallbackCommand cmd, String callbackData) {
            gotCallback(cmd.signCallback(callbackData));
            return this;
        }

        public GivenSpec defaultAction(UpdateHandler defaultConsumer) {
            this.defaultConsumer = defaultConsumer;
            return this;
        }

        public GivenSpec from(int id, String firstName) {
            from(id, firstName, "");
            return this;
        }

        public GivenSpec from(int id, String firstName, String lastName) {
            user.setId(id);
            user.setFirst_name(firstName);
            user.setLast_name(lastName);
            return this;
        }

        public GivenSpec from(String firstName, String lastName) {
            from(1, firstName, lastName);
            return this;
        }

        public ThenSpec then(CallbackCommand cmd) {
            return new ThenSpec(getRequests(), cmd);
        }

        public ThenSpec then() {
            return then(null);
        }

        //TODO refactor
        private List<BaseRequest> getRequests() {
            GetUpdatesResponse updatesResponse = new GetUpdatesResponse();
            Update update = new Update();
            update.setUpdate_id(1);
            if (callbackData.isEmpty()) {
                update.setMessage(createMessage(1, text, chat, user, date));
            } else {
                CallbackQuery callback_query = new CallbackQuery();
                callback_query.setFrom(user);
                callback_query.setMessage(createMessage(originalMsg, text, chat, user, date));
                callback_query.setData(callbackData);
                update.setCallback_query(callback_query);
            }
            updatesResponse.setResult(singletonList(update));
            FakeBotApi botApi = new FakeBotApi(updatesResponse);
            TelegramBot bot = new SimpleBot(botApi);
            startOnce(bot);
            return botApi.requests();
        }

        private void startOnce(TelegramBot bot) {
            HandlersChainListener listener = new HandlersChainListener(bot, defaultConsumer, handlers);
            bot.setUpdatesListener(new OneTimeListenerDecorator(bot, listener));
        }
    }

    private static Message createMessage(int id, String text, Chat chat, User user, Integer date) {
        Message m = new Message();
        m.setMessage_id(id);
        m.setText(text);
        m.setChat(chat);
        m.setFrom(user);
        m.setDate(date);
        m.setEntities(parseEntities(text).toArray(new MessageEntity[0]));
        return m;
    }

    private static List<MessageEntity> parseEntities(String text) {
        VerbalExpression cmdExp = regex().find("/").oneOrMore().word().build();
        return cmdExp.getTextGroups(text, 0).stream().map(
                s ->  createMessageEntity(s.length(), 0)).collect(toList());
    }

    private static MessageEntity createMessageEntity(Integer length, Integer offset) {
        MessageEntity me = new MessageEntity();
        me.setLength(length);
        me.setOffset(offset);
        me.setType(MessageEntity.Type.bot_command);
        return me;
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
        private final CallbackCommand cmd;
        private final RequestMatcher matcher = new RequestMatcher();

        ThenSpec(List<BaseRequest> actualRequests, CallbackCommand cmd) {
            this.actualRequests = actualRequests;
            this.cmd = cmd;
        }

        public void shouldAnswer(BaseRequest... expectedRequests) {
            if (cmd != null) {
                List<BaseRequest> collect = new ArrayList<>();
                stream(expectedRequests).forEach(r -> collect.add(cmd.signRequest(r)));
                expectedRequests = collect.toArray(new BaseRequest[expectedRequests.length]);
            }
            List<RequestMatcher.Mismatch> mismatches = matcher.match(expectedRequests, actualRequests);
            if (!mismatches.isEmpty()) {
                throw new AssertionError(mismatches);
            }
        }

        public void noAnswer() {
            if (!actualRequests.isEmpty()) {
                throw new AssertionError("No answer was expected, but got: \n " + actualRequests);
            }
        }
    }
}