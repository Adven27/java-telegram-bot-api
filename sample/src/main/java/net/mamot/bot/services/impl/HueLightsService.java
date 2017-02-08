package net.mamot.bot.services.impl;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.JsonResponse;
import net.mamot.bot.services.LightsService;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.meta.Device;

import javax.json.JsonValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

import static com.jcabi.http.Request.PUT;

public class HueLightsService implements LightsService {

    private static final String API = "api/";

    //TODO implement authorization process https://developers.meethue.com/documentation/getting-started
    private String user = "8UgJzAC5eHazzKdlDWg2voNUilbMJ6qKaIAZjQt3";
    private final String bridgeURL;

    public HueLightsService() {
        this.bridgeURL = getBridges().iterator().next().getDetails().getBaseURL().toString();
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

    public void turnOffAll() {
        doForEachLight((id, value) ->  turnOff(id));
    }

    @Override
    public boolean hasOnLights() {
        return false;
    }

    private void doForEachLight(BiConsumer<String, JsonValue> consumer) {
        try {
            new JdkRequest(bridgeURL + API + user + "/lights").fetch().
                    as(JsonResponse.class).json().readObject().forEach(consumer);
        } catch (IOException e) {
            throw new BridgeUnreachableEx(e);
        }
    }

    private void changeState(String body, final String id) {
        try {
            new JdkRequest(bridgeURL + API + user + "/lights/" + id + "/state").
                    method(PUT).body().set(body).back().fetch();
        } catch (IOException e) {
            throw new BridgeUnreachableEx(e);
        }
    }

    private Collection<Device> getBridges()  {
        UpnpService upnpService = new UpnpServiceImpl();
        upnpService.getControlPoint().search();
        sleep(2000);
        Collection<Device> devices = new ArrayList<>(upnpService.getRegistry().getDevices());
        devices.removeIf(d -> notHue(d));
        upnpService.shutdown();
        return devices;
    }

    private boolean notHue(Device d) {
        return !d.getDetails().getModelDetails().getModelDescription().equals("Philips hue Personal Wireless Lighting");
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
