package com.github.pokeclash.cobblewand.network.server.handler;

import com.github.pokeclash.cobblewand.CobbleWand;
import com.github.pokeclash.cobblewand.component.CobbleWandComponents;
import com.github.pokeclash.cobblewand.component.utils.PokemonStorage;
import com.github.pokeclash.cobblewand.item.CobbleWandItems;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonSetPacket;
import com.github.pokeclash.cobblewand.permission.CobbleWandPermissionService;
import com.github.pokeclash.cobblewand.permission.CobbleWandPermissions;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class PokemonSetHandler {
    public static void handle(PokemonSetPacket pokemonSetPacket, NetworkManager.PacketContext packetContext) {
        if (!(packetContext.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (!CobbleWandPermissionService.hasPermission(serverPlayer, CobbleWandPermissions.POKEMON_SET)) {
            CobbleWandPermissionService.sendDeniedMessage(serverPlayer, CobbleWandPermissions.POKEMON_SET);
            return;
        }

        if (pokemonSetPacket.wandData().flags().flatMap(com.github.pokeclash.cobblewand.codec.WandData.Flags::statue).isPresent()
                && !CobbleWandPermissionService.hasPermission(serverPlayer, CobbleWandPermissions.STATUE_SET)) {
            CobbleWandPermissionService.sendDeniedMessage(serverPlayer, CobbleWandPermissions.STATUE_SET);
            return;
        }

        ItemStack itemStack = packetContext.getPlayer().getMainHandItem();
        if (!itemStack.is(CobbleWandItems.COBBLEWAND.get())) {
            return;
        }

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
