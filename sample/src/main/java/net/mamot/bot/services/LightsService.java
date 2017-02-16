package net.mamot.bot.services;

public interface LightsService {
    void turnOffAll();

    void turnOnAll();

    BridgeInfo bridgeInfo();

    class BridgeUnreachableEx extends RuntimeException {
        public BridgeUnreachableEx(Exception e) {
            super(e);
        }
    }

    class BridgeInfo {
        final String desc;

        public BridgeInfo(String desc) {
            this.desc = desc;
        }

        public String desc() {
            return desc;
        }
    }
}
