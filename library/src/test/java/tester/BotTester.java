package tester;

import com.pengrad.telegrambot.MyTelegramBot;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import ru.lanwen.verbalregex.VerbalExpression;

import java.util.*;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static ru.lanwen.verbalregex.VerbalExpression.regex;

public class BotTester {

    public static GivenSpec got(String txt) {
        return new GivenSpec(txt);
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

        public GivenSpec(String text) {
            this.text = text;
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
            new MyTelegramBot(new TelegramBot(botApi)).doOnce();
            return new ThenSpec(botApi.requests());
        }
    }

    public static class ThenSpec {
        private final List<BaseRequest> requests;
        private final Map<BaseRequest, List<Mismatch>> mismatches;

        public ThenSpec(List<BaseRequest> requests) {
            this.requests = requests;
            mismatches = new HashMap<>();
        }

        public void answer(SendMessage sendMessage) {
            mismatches.clear();
            for (BaseRequest br : requests) {
                if (br instanceof SendMessage) {
                    List<Mismatch> msms = new ArrayList<>();
                    SendMessage actual = (SendMessage) br;
                    for (Map.Entry<String, Object> param : actual.getParameters().entrySet()) {
                        String exp = sendMessage.getParameters().get(param.getKey()).toString();
                        String act = param.getValue().toString();
                        if (!act.equals(exp)) {
                            msms.add(new Mismatch(param.getKey(), exp, act));
                        }
                    }
                    if (!msms.isEmpty()) {
                        mismatches.put(br, msms);
                    }
                }
            }
            if (requests.size() == mismatches.size()) {
                throw new AssertionError("not found");
            }
        }

        private class Mismatch {
            private final String key;
            private final String exp;
            private final String act;

            public Mismatch(String key, String exp, String act) {
                this.key = key;
                this.exp = exp;
                this.act = act;
            }
        }
    }
}