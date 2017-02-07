package net.mamot.bot.services;

public interface LightsService {
    void turnOffAll();

    boolean shOffLights();

    boolean hasOnLights();

    class Light {
        final int id;
        final boolean isOn;

        public Light(int id, boolean isOn) {
            this.id = id;
            this.isOn = isOn;
        }
    }
}
