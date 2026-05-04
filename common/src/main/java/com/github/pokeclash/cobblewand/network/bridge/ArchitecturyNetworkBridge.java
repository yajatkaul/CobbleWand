package com.github.pokeclash.cobblewand.network.bridge;

import com.github.pokeclash.cobblewand.CobbleWand;
import com.github.pokeclash.cobblewand.network.server.handler.OpenWandMenuHandler;
import com.github.pokeclash.cobblewand.network.server.handler.OpenWandMenuRequestHandler;
import com.github.pokeclash.cobblewand.network.server.handler.PokemonAddHandler;
import com.github.pokeclash.cobblewand.network.server.handler.PokemonEditHandler;
import com.github.pokeclash.cobblewand.network.server.handler.PokemonSetHandler;
import com.github.pokeclash.cobblewand.network.server.packet.OpenWandMenuPacket;
import com.github.pokeclash.cobblewand.network.server.packet.OpenWandMenuRequestPacket;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonAddPacket;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonEditPacket;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonSetPacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

final class ArchitecturyNetworkBridge implements CobbleWandNetworkBridge.NetworkBridge {
    private boolean openWandMenuS2CRegistered = false;

    @Override
    public void register() {
        registerC2S();
        registerS2C();
    }

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        NetworkManager.sendToServer(payload);
    }

    @Override
    public void sendOpenWandMenuToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        if (!openWandMenuS2CRegistered) {
            return;
        }
        NetworkManager.sendToPlayer(player, payload);
    }

    @Override
    public boolean canSendOpenWandMenuPacket() {
        return openWandMenuS2CRegistered;
    }

    private void registerC2S() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                OpenWandMenuRequestPacket.TYPE,
                OpenWandMenuRequestPacket.STREAM_CODEC,
                OpenWandMenuRequestHandler::handle
        );

        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                PokemonSetPacket.TYPE,
                PokemonSetPacket.STREAM_CODEC,
                PokemonSetHandler::handle
        );

        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                PokemonAddPacket.TYPE,
                PokemonAddPacket.STREAM_CODEC,
                PokemonAddHandler::handle
        );

        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                PokemonEditPacket.TYPE,
                PokemonEditPacket.STREAM_CODEC,
                PokemonEditHandler::handle
        );
    }

    private void registerS2C() {
        try {
            NetworkManager.registerReceiver(
                    NetworkManager.Side.S2C,
                    OpenWandMenuPacket.TYPE,
                    OpenWandMenuPacket.STREAM_CODEC,
                    OpenWandMenuHandler::handle
            );
            openWandMenuS2CRegistered = true;
        } catch (AbstractMethodError | NoSuchMethodError error) {
            openWandMenuS2CRegistered = false;
            CobbleWand.LOGGER.error("Failed to register S2C packet '{}'. Architectury runtime appears incompatible; menu sync packet sending will be disabled.", OpenWandMenuPacket.PACKET_ID, error);
        }
    }
}
