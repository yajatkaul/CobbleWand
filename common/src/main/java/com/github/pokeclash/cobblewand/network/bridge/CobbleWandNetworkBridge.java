package com.github.pokeclash.cobblewand.network.bridge;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public final class CobbleWandNetworkBridge {
    private static final NetworkBridge BRIDGE = new ArchitecturyNetworkBridge();

    private CobbleWandNetworkBridge() {
    }

    public static void register() {
        BRIDGE.register();
    }

    public static void sendToServer(CustomPacketPayload payload) {
        BRIDGE.sendToServer(payload);
    }

    public static void sendOpenWandMenuToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        BRIDGE.sendOpenWandMenuToPlayer(player, payload);
    }

    public static boolean canSendOpenWandMenuPacket() {
        return BRIDGE.canSendOpenWandMenuPacket();
    }

    interface NetworkBridge {
        void register();

        void sendToServer(CustomPacketPayload payload);

        void sendOpenWandMenuToPlayer(ServerPlayer player, CustomPacketPayload payload);

        boolean canSendOpenWandMenuPacket();
    }
}
