package com.github.pokeclash.cobblewand.platform.neoforge;

import com.github.pokeclash.cobblewand.permission.CobbleWandPermissions;
import net.minecraft.server.level.ServerPlayer;

import java.lang.reflect.Method;
import java.util.UUID;

public class CobbleWandPermissionPlatformImpl {
    public static boolean hasPermission(ServerPlayer player, String node) {
        Boolean bukkitResult = hasBukkitPermission(player.getUUID(), node);
        if (bukkitResult != null) {
            return bukkitResult;
        }

        Boolean luckPermsResult = hasLuckPermsPermission(player.getUUID(), node);
        if (luckPermsResult != null) {
            return luckPermsResult;
        }

        return player.hasPermissions(2);
    }

    public static void registerPermissionNodes() {
        try {
            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
            Method getPluginManager = bukkitClass.getMethod("getPluginManager");
            Object pluginManager = getPluginManager.invoke(null);
            if (pluginManager == null) {
                return;
            }

            Class<?> permissionClass = Class.forName("org.bukkit.permissions.Permission");
            Class<?> permissionDefaultClass = Class.forName("org.bukkit.permissions.PermissionDefault");
            Object opDefault = permissionDefaultClass.getField("OP").get(null);

            Method addPermission = pluginManager.getClass().getMethod("addPermission", permissionClass);
            Method getPermission = pluginManager.getClass().getMethod("getPermission", String.class);

            String[] nodes = new String[]{
                    CobbleWandPermissions.MENU_OPEN,
                    CobbleWandPermissions.POKEMON_SET,
                    CobbleWandPermissions.PARTY_ADD,
                    CobbleWandPermissions.STATUE_SET
            };

            for (String node : nodes) {
                Object existing = getPermission.invoke(pluginManager, node);
                if (existing != null) {
                    continue;
                }
                Object permission = permissionClass
                        .getConstructor(String.class, permissionDefaultClass)
                        .newInstance(node, opDefault);
                addPermission.invoke(pluginManager, permission);
            }
        } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException | LinkageError ignored) {
        }
    }

    private static Boolean hasBukkitPermission(UUID playerUuid, String node) {
        try {
            Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
            Method getPlayer = bukkitClass.getMethod("getPlayer", UUID.class);
            Object bukkitPlayer = getPlayer.invoke(null, playerUuid);
            if (bukkitPlayer == null) {
                return null;
            }

            Method hasPermission = bukkitPlayer.getClass().getMethod("hasPermission", String.class);
            return (Boolean) hasPermission.invoke(bukkitPlayer, node);
        } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException | LinkageError ignored) {
            return null;
        }
    }

    private static Boolean hasLuckPermsPermission(UUID playerUuid, String node) {
        try {
            Class<?> providerClass = Class.forName("net.luckperms.api.LuckPermsProvider");
            Object luckPerms = providerClass.getMethod("get").invoke(null);
            if (luckPerms == null) {
                return null;
            }

            Object userManager = luckPerms.getClass().getMethod("getUserManager").invoke(luckPerms);
            Object user = userManager.getClass().getMethod("getUser", UUID.class).invoke(userManager, playerUuid);
            if (user == null) {
                return null;
            }

            Object cachedData = user.getClass().getMethod("getCachedData").invoke(user);
            Object permissionData = cachedData.getClass().getMethod("getPermissionData").invoke(cachedData);
            Object tristate = permissionData.getClass().getMethod("checkPermission", String.class).invoke(permissionData, node);
            return (Boolean) tristate.getClass().getMethod("asBoolean").invoke(tristate);
        } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException | LinkageError ignored) {
            return null;
        }
    }
}
