package com.github.pokeclash.cobblewand.permission;

import com.github.pokeclash.cobblewand.platform.CobbleWandPermissionPlatform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class CobbleWandPermissionService {
    private CobbleWandPermissionService() {
    }

    public static boolean hasPermission(ServerPlayer player, String node) {
        return CobbleWandPermissionPlatform.hasPermission(player, node);
    }

    public static void sendDeniedMessage(Player player, String node) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("No tienes permiso: " + node));
    }

    public static void registerBukkitPermissions() {
        CobbleWandPermissionPlatform.registerPermissionNodes();
    }
}
