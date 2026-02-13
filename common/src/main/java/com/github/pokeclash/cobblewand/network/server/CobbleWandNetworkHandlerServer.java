package com.github.pokeclash.cobblewand.network.server;

import com.github.pokeclash.cobblewand.network.server.handler.PokemonSetHandler;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonSetPacket;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;

public class CobbleWandNetworkHandlerServer {
    public static void register() {
        if (Platform.getEnv() == EnvType.SERVER) {
            registerServerOnly();
        }

        registerCommon();
    }

    public static void registerServerOnly() {

    }

    public static void registerCommon() {
        NetworkManager.registerReceiver(
                NetworkManager.Side.C2S,
                PokemonSetPacket.TYPE,
                PokemonSetPacket.STREAM_CODEC,
                PokemonSetHandler::handle
        );
    }
}
