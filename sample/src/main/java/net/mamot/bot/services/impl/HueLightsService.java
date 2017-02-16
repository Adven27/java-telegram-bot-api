package net.mamot.bot.services.impl;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.JsonResponse;
import net.mamot.bot.services.LightsService;

import javax.json.JsonValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static com.jcabi.http.Request.PUT;

public class HueLightsService implements LightsService {

    private static final String API = "api/";
    private final BridgeFinder bridgeFinder;

    //TODO implement authorization process https://developers.meethue.com/documentation/getting-started
    private String user = "8UgJzAC5eHazzKdlDWg2voNUilbMJ6qKaIAZjQt3";
    private Optional<BridgeInfo> currentBridge = Optional.empty();

    public HueLightsService(BridgeFinder bridgeFinder) {
        this.bridgeFinder = bridgeFinder;
    }

    public void turnOff(String id) {
        changeState("{\"on\":false}", id);
    }

    public void turnOn(String id) {
        changeState("{\"on\":true}", id);
    }

    public void turnOnAll() {
        doForEachLight((id, value) ->  turnOn(id));
    }

    @Override
    public Optional<BridgeInfo> currentBridge() {
        return currentBridge;
    }

    @Override
    public void currentBridge(BridgeInfo bridge) {
        currentBridge = Optional.ofNullable(bridge);
    }

    @Override
    public List<BridgeInfo> searchBridges() {
        List<BridgeInfo> bridges = new ArrayList<>();
        bridgeFinder.search().forEach(d -> bridges.add(new BridgeInfo(d.getIdentity().getUdn().getIdentifierString(), d.getDisplayString(), d.getDetails().getBaseURL().toString())));
        return bridges;
    }

    public void turnOffAll() {
        doForEachLight((id, value) ->  turnOff(id));
    }

    private void doForEachLight(BiConsumer<String, JsonValue> consumer) {
        try {
            new JdkRequest(currentBridge.get().url() + API + user + "/lights").fetch().
                    as(JsonResponse.class).json().readObject().forEach(consumer);
        } catch (IOException e) {
            throw new BridgeUnreachableEx(e);
        }
    }

    private void changeState(String body, final String id) {
        try {
            new JdkRequest(currentBridge.get().url() + API + user + "/lights/" + id + "/state").
                    method(PUT).body().set(body).back().fetch();
        } catch (IOException e) {
            throw new BridgeUnreachableEx(e);
        }
    }
}
