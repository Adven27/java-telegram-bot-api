package net.mamot.bot.services;

public interface LightsService {
    void turnOffAll();

    void turnOnAll();

    boolean hasOnLights();

    class Light {
        final int id;
        final boolean isOn;

        public Light(int id, boolean isOn) {
            this.id = id;
            this.isOn = isOn;
        }
    }

    class BridgeUnreachableEx extends RuntimeException {
        public BridgeUnreachableEx(Exception e) {
            super(e);
        }
    }
}
