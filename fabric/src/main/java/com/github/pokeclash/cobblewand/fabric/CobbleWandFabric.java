package com.github.pokeclash.cobblewand.fabric;

import com.github.pokeclash.cobblewand.CobbleWand;
import net.fabricmc.api.ModInitializer;

public final class CobbleWandFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CobbleWand.init();
    }
}
