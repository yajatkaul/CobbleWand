package com.github.pokeclash.cobblewand;

import com.github.pokeclash.cobblewand.component.CobbleWandComponents;
import com.github.pokeclash.cobblewand.creative.CobbleWandTab;
import com.github.pokeclash.cobblewand.item.CobbleWandItems;
import com.github.pokeclash.cobblewand.network.server.CobbleWandNetworkHandlerServer;
import dev.architectury.event.events.common.LifecycleEvent;
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
        });
    }
}
