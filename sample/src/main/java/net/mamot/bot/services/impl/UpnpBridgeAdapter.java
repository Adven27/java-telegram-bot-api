package net.mamot.bot.services.impl;

import net.mamot.bot.services.BridgeAdapter;
import net.mamot.bot.services.HueBridge;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UpnpBridgeAdapter implements BridgeAdapter {
    public UpnpBridgeAdapter() {
    }

    public List<HueBridge> search() {
        UpnpService upnpService = new UpnpServiceImpl();
        upnpService.getControlPoint().search();
        sleep(2000);
        Collection<Device> devices = new ArrayList<>(upnpService.getRegistry().getDevices());
        devices.removeIf(this::notHue);
        upnpService.shutdown();

        List<HueBridge> bridges = new ArrayList<>();
        devices.forEach(d -> bridges.add(new RestfulHueBridge(d.getIdentity().getUdn().getIdentifierString(), d.getDisplayString(), d.getDetails().getBaseURL().toString())));

        return bridges;
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