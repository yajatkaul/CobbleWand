package com.github.pokeclash.cobblewand.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.level.ServerPlayer;

public class CobbleWandPermissionPlatform {
    @ExpectPlatform
    public static boolean hasPermission(ServerPlayer player, String node) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void registerPermissionNodes() {
        throw new AssertionError();
    }
}
