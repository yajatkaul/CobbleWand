package com.github.pokeclash.cobblewand.network.server.handler;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.github.pokeclash.cobblewand.item.CobbleWandItems;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonAddPacket;
import com.github.pokeclash.cobblewand.permission.CobbleWandPermissionService;
import com.github.pokeclash.cobblewand.permission.CobbleWandPermissions;
import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class PokemonAddHandler {
    public static void handle(PokemonAddPacket pokemonAddPacket, NetworkManager.PacketContext packetContext) {
        if (packetContext.getPlayer() instanceof ServerPlayer serverPlayer) {
            ItemStack itemStack = serverPlayer.getMainHandItem();
            if (!itemStack.is(CobbleWandItems.COBBLEWAND.get())) {
                return;
            }

            if (!CobbleWandPermissionService.hasPermission(serverPlayer, CobbleWandPermissions.PARTY_ADD)) {
                CobbleWandPermissionService.sendDeniedMessage(serverPlayer, CobbleWandPermissions.PARTY_ADD);
                return;
            }

            PokemonProperties properties = pokemonAddPacket.pokemon().createPokemonProperties(PokemonPropertyExtractor.ALL);
            Pokemon pokemon = properties.create();
            Cobblemon.INSTANCE.getStorage().getParty(serverPlayer).add(pokemon);
        }
    }
}
