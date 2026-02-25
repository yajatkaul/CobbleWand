package com.github.pokeclash.cobblewand.network.server.handler;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonAddPacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;

public class PokemonAddHandler {
    public static void handle(PokemonAddPacket pokemonAddPacket, NetworkManager.PacketContext packetContext) {
        if (packetContext.getPlayer() instanceof ServerPlayer serverPlayer) {
            PokemonProperties properties = pokemonAddPacket.pokemon().createPokemonProperties(PokemonPropertyExtractor.ALL);
            Pokemon pokemon = properties.create();
            Cobblemon.INSTANCE.getStorage().getParty(serverPlayer).add(pokemon);
        }
    }
}
