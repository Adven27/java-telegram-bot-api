package net.mamot.bot.services.lights;

public interface HueBridge {
    String id();
    String desc();
    void turnOn(String id) throws BridgeUnreachableEx;
    void turnOff(String id) throws BridgeUnreachableEx;
    void turnOnAll() throws BridgeUnreachableEx;
    void turnOffAll() throws BridgeUnreachableEx;

    class BridgeUnreachableEx extends RuntimeException {
        public BridgeUnreachableEx(Exception e) {
            super(e);
        }
    }

}
