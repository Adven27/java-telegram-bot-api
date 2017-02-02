package e2e;

import com.google.gson.Gson;
import com.pengrad.telegrambot.BotAPI;
import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.MyTelegramBot;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;

import java.lang.reflect.Type;
import java.util.*;

public class BotTester {


    public static GivenSpec given() {
        return new GivenSpec();
    }

    public static class GivenSpec {
        private String text = "";
        private long date = 1485957820;
        private String user = "{" +
                "\"id\": 229669496," +
                "\"first_name\": \"Nikolay\"," +
                "\"last_name\": \"Toropanov\"," +
                "\"username\": \"niktoro\"" +
                "}";
        private String chat = "{" +
                "\"id\": 1," +
                "\"first_name\": \"Nikolay\"," +
                "\"last_name\": \"Toropanov\"," +
                "\"username\": \"niktoro\"," +
                "\"type\": \"private\"" +
                "}";

        private String updateReq() {
            return "{" +
                    "    \"ok\":true," +
                    "    \"result\":" +
                    "    [{" +
                    "        \"update_id\": 563614790," +
                    "        \"message\": {" +
                    "            \"message_id\": 373," +
                    "            \"from\": " + user + "," +
                    "            \"chat\": " + chat + "," +
                    "            \"date\": " + date + "," +
                    "            \"text\": \"" + text + "\"" +
                    "        }" +
                    "    }]" +
                    "}";
        }

        public GivenSpec chat(String c) {
            chat =  c;
            return this;
        }

        public GivenSpec from(String u) {
            user = u;
            return this;
        }

        public GivenSpec text(String txt) {
            text = txt;
            return this;
        }

        public GivenSpec date(Date dt) {
            date = dt.getTime();
            return this;
        }

        public ThenSpec then() {
            FakeBotApi botApi = new FakeBotApi(updateReq());
            new MyTelegramBot(new TelegramBot(botApi)).doOnce();
            return new ThenSpec(botApi.requests());
        }
    }

    public static class ThenSpec {
        private final List<BaseRequest> requests;
        private final Map<BaseRequest, List<Mismatch>> mismatches;

        public ThenSpec(List<BaseRequest> requests) {
            this.requests = requests;
            mismatches = new HashMap<BaseRequest, List<Mismatch>>();
        }

        public void shouldHave(SendMessage sendMessage) {
            mismatches.clear();
            for (BaseRequest br : requests) {
                if (br instanceof SendMessage) {
                    List<Mismatch> msms = new ArrayList<Mismatch>();
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


    private static class FakeBotApi implements BotAPI {
        private final String updateReq;
        List<BaseRequest> requests = new ArrayList<BaseRequest>();

        public FakeBotApi(String updateReq) {
            this.updateReq = updateReq;
        }

        public List<BaseRequest> requests() {
            return requests;
        }

        @Override
        public <T extends BaseRequest, R extends BaseResponse> void send(T request, Callback<T, R> callback) {
            R response = null;
            if (request instanceof GetUpdates) {
                response = getUpdate(GetUpdatesResponse.class);
            } else {
                requests.add(request);
            }
            callback.onResponse(request, response);
        }

        @Override
        public <T extends BaseRequest, R extends BaseResponse> R send(BaseRequest<T, R> request) {
            R response = null;
            if (request instanceof GetUpdates) {
                response = getUpdate(GetUpdatesResponse.class);
            } else {
                requests.add(request);
            }
            return response;
        }

        private <R extends BaseResponse> R getUpdate(Type type) {
            return new Gson().fromJson(updateReq, type);
        }
    }

}
