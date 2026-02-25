package com.github.pokeclash.cobblewand.network.server.handler;

import com.cobblemon.mod.common.Cobblemon;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonAddPacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;

public class PokemonAddHandler {
    public static void handle(PokemonAddPacket pokemonAddPacket, NetworkManager.PacketContext packetContext) {
        if (packetContext.getPlayer() instanceof ServerPlayer serverPlayer) {
            Cobblemon.INSTANCE.getStorage().getParty(serverPlayer).add(pokemonAddPacket.pokemon());
        }
    }
}
