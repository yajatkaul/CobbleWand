package com.github.pokeclash.cobblewand.network.server.handler;

import com.github.pokeclash.cobblewand.CobbleWand;
import com.github.pokeclash.cobblewand.component.CobbleWandComponents;
import com.github.pokeclash.cobblewand.component.utils.PokemonStorage;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonSetPacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class PokemonSetHandler {
    public static void handle(PokemonSetPacket pokemonSetPacket, NetworkManager.PacketContext packetContext) {
        ItemStack itemStack = packetContext.getPlayer().getMainHandItem();
        itemStack.set(
                CobbleWandComponents.POKEMON_STORAGE.get(),
                new PokemonStorage(new CompoundTag(), pokemonSetPacket.wandData())
                        .save(
                                CobbleWand.minecraftServer.registryAccess(),
                                pokemonSetPacket.pokemon()
                        )
        );
    }
}
