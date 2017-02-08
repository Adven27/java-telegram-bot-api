package net.mamot.bot.services.impl;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.JsonResponse;
import net.mamot.bot.services.LightsService;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static com.jcabi.http.Request.PUT;

public class HueLightsService implements LightsService {

    private String user = "8UgJzAC5eHazzKdlDWg2voNUilbMJ6qKaIAZjQt3";
    private String bridgeURL = "192.168.0.4";

    List<Light> lights = new ArrayList<>();

    public void turnOff(String id) {
        changeState("{\"on\":false}", id);
    }

    public void turnOn(String id) {
        changeState("{\"on\":true}", id);
    }

    public void turnOnAll() {
        doForEachLight((id, value) ->  turnOn(id));
    }

    public void turnOffAll() {
        doForEachLight((id, value) ->  turnOff(id));
    }

    @Override
    public boolean hasOnLights() {
        return false;
    }

    private void doForEachLight(BiConsumer<String, JsonValue> consumer) {
        try {
            JsonObject jsonObject = new JdkRequest("http://" + bridgeURL + "/api/" + user + "/lights").fetch().as(JsonResponse.class).json().readObject();
            jsonObject.forEach(consumer);
        } catch (IOException e) {
            throw new BridgeUnreachableEx(e);
        }
    }

    private void changeState(String body, final String id) {
        try {
            new JdkRequest("http://" + bridgeURL + "/api/" + user + "/lights/" + id + "/state").
                    method(PUT).body().set(body).back().fetch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
