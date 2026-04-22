package com.github.pokeclash.cobblewand.network.server.handler;

import com.github.pokeclash.cobblewand.network.server.packet.OpenWandMenuPacket;
import com.github.pokeclash.cobblewand.platform.CobbleWandClient;
import dev.architectury.networking.NetworkManager;

public class OpenWandMenuHandler {
    public static void handle(OpenWandMenuPacket packet, NetworkManager.PacketContext packetContext) {
        packetContext.queue(() -> CobbleWandClient.openWandScreen(packet.wandData()));
    }
}
