package net.mamot.bot.services;

import java.util.List;
import java.util.Optional;

public interface LightsService {
    void turnOffAll();

    void turnOnAll();

    Optional<BridgeInfo> currentBridge();

    void currentBridge(BridgeInfo bridge);

    List<BridgeInfo> searchBridges();

    class BridgeUnreachableEx extends RuntimeException {
        public BridgeUnreachableEx(Exception e) {
            super(e);
        }
    }

    class BridgeInfo {
        final String desc;
        final String id;
        final String url;

        public BridgeInfo(String id, String desc, String url) {
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
    }
}
