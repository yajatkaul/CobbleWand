package com.github.pokeclash.cobblewand.network.server;

import com.github.pokeclash.cobblewand.network.bridge.CobbleWandNetworkBridge;

public class CobbleWandNetworkHandlerServer {
    public static void register() {
        CobbleWandNetworkBridge.register();
    }

    public static boolean canSendOpenWandMenuPacket() {
        return CobbleWandNetworkBridge.canSendOpenWandMenuPacket();
    }
}
