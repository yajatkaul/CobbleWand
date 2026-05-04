package com.github.pokeclash.cobblewand;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleStartedEvent;
import com.github.pokeclash.cobblewand.component.CobbleWandComponents;
import com.github.pokeclash.cobblewand.creative.CobbleWandTab;
import com.github.pokeclash.cobblewand.item.CobbleWandItems;
import com.github.pokeclash.cobblewand.network.server.CobbleWandNetworkHandlerServer;
import com.github.pokeclash.cobblewand.permission.CobbleWandPermissionService;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CobbleWand {
    public static final String MOD_ID = "cobblewand";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static MinecraftServer minecraftServer;

    public static void init() {
        CobbleWandItems.register();
        CobbleWandTab.register();
        CobbleWandComponents.register();
        CobbleWandNetworkHandlerServer.register();
        LifecycleEvent.SERVER_STARTED.register((server) -> {
            minecraftServer = server;
            CobbleWandPermissionService.registerBukkitPermissions();
        });

        CobblemonEvents.BATTLE_STARTED_PRE.subscribe(Priority.NORMAL, CobbleWand::battlePre);
    }

    private static void battlePre(BattleStartedEvent.Pre pre) {
        pre.getBattle().getActors().forEach((battleActor -> {
            battleActor.getPokemonList().forEach((battlePokemon) -> {
                if (battlePokemon.getOriginalPokemon().getAspects().contains("cant_battle")) {
                    pre.setReason(Component.literal("Some of the pokemon(s) are not supposed to battle"));
                    pre.cancel();
                }
            });
        }));
    }
}
