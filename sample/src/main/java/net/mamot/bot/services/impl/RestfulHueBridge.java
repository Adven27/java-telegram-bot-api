package net.mamot.bot.services.impl;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.JsonResponse;
import net.mamot.bot.services.HueBridge;

import javax.json.JsonValue;
import java.io.IOException;
import java.util.function.BiConsumer;

import static com.jcabi.http.Request.PUT;

public class RestfulHueBridge implements HueBridge {
    private static final String API = "api/";

    final String desc;
    final String id;
    final String url;

    //TODO implement authorization process https://developers.meethue.com/documentation/getting-started
    private String user = "8UgJzAC5eHazzKdlDWg2voNUilbMJ6qKaIAZjQt3";

    public RestfulHueBridge(String id, String desc, String url) {
        this.id = id;
        this.desc = desc;
        this.url = url;
    }

    public String desc() {
        return desc;
    }

    public String id() {
        return id;
    }

    public String url() {
        return url;
    }

    public void turnOn(String id) {
        changeState("{\"on\":true}", id);
    }

    public void turnOff(String id) {
        changeState("{\"on\":false}", id);
    }

    public void turnOnAll() {
        doForEachLight((id, value) ->  turnOn(id));
    }

    public void turnOffAll() {
        doForEachLight((id, value) ->  turnOff(id));
    }

    private void doForEachLight(BiConsumer<String, JsonValue> consumer) {
        try {
            new JdkRequest(url + API + user + "/lights").fetch().
                    as(JsonResponse.class).json().readObject().forEach(consumer);
        } catch (IOException e) {
            throw new BridgeUnreachableEx(e);
        }
    }

    private void changeState(String body, final String id) {
        try {
            new JdkRequest(url + API + user + "/lights/" + id + "/state").
                    method(PUT).body().set(body).back().fetch();
        } catch (IOException e) {
            throw new BridgeUnreachableEx(e);
        }
    }
}