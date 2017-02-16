package net.mamot.bot.services.impl;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;
import java.util.Collection;

public class BridgeFinder {
    public BridgeFinder() {
    }

    Collection<Device> search() {
        UpnpService upnpService = new UpnpServiceImpl();
        upnpService.getControlPoint().search();
        sleep(2000);
        Collection<Device> devices = new ArrayList<Device>(upnpService.getRegistry().getDevices());
        devices.removeIf(d -> notHue(d));
        upnpService.shutdown();
        return devices;
    }

    boolean notHue(Device d) {
        return !d.getDetails().getModelDetails().getModelDescription().equals("Philips hue Personal Wireless Lighting");
    }

    void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}