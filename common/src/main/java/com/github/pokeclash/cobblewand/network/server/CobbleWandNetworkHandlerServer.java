package com.github.pokeclash.cobblewand.network.server;

import com.github.pokeclash.cobblewand.network.server.handler.PokemonAddHandler;
import com.github.pokeclash.cobblewand.network.server.handler.OpenWandMenuHandler;
import com.github.pokeclash.cobblewand.network.server.handler.OpenWandMenuRequestHandler;
import com.github.pokeclash.cobblewand.network.server.handler.PokemonEditHandler;
import com.github.pokeclash.cobblewand.network.server.handler.PokemonSetHandler;
import com.github.pokeclash.cobblewand.network.server.packet.OpenWandMenuPacket;
import com.github.pokeclash.cobblewand.network.server.packet.OpenWandMenuRequestPacket;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonAddPacket;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonEditPacket;
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
                OpenWandMenuRequestPacket.TYPE,
                OpenWandMenuRequestPacket.STREAM_CODEC,
                OpenWandMenuRequestHandler::handle
        );

        NetworkManager.registerReceiver(
                NetworkManager.Side.S2C,
                OpenWandMenuPacket.TYPE,
                OpenWandMenuPacket.STREAM_CODEC,
                OpenWandMenuHandler::handle
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
}
