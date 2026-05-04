package com.github.pokeclash.cobblewand.network.server.handler;

import com.github.pokeclash.cobblewand.component.CobbleWandComponents;
import com.github.pokeclash.cobblewand.component.utils.PokemonStorage;
import com.github.pokeclash.cobblewand.item.CobbleWandItems;
import com.github.pokeclash.cobblewand.network.bridge.CobbleWandNetworkBridge;
import com.github.pokeclash.cobblewand.network.server.packet.OpenWandMenuPacket;
import com.github.pokeclash.cobblewand.network.server.packet.OpenWandMenuRequestPacket;
import com.github.pokeclash.cobblewand.permission.CobbleWandPermissionService;
import com.github.pokeclash.cobblewand.permission.CobbleWandPermissions;
import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class OpenWandMenuRequestHandler {
    public static void handle(OpenWandMenuRequestPacket ignored, NetworkManager.PacketContext packetContext) {
        if (!(packetContext.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (!CobbleWandPermissionService.hasPermission(serverPlayer, CobbleWandPermissions.MENU_OPEN)) {
            CobbleWandPermissionService.sendDeniedMessage(serverPlayer, CobbleWandPermissions.MENU_OPEN);
            return;
        }

        ItemStack mainHand = serverPlayer.getMainHandItem();
        if (!mainHand.is(CobbleWandItems.COBBLEWAND.get())) {
            return;
        }
        if (!CobbleWandNetworkBridge.canSendOpenWandMenuPacket()) {
            return;
        }

        PokemonStorage storage = mainHand.getOrDefault(CobbleWandComponents.POKEMON_STORAGE.get(), PokemonStorage.defaultStorage());
        CobbleWandNetworkBridge.sendOpenWandMenuToPlayer(serverPlayer, new OpenWandMenuPacket(storage.wandData()));
    }
}
