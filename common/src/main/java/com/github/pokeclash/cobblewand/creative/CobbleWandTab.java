package com.github.pokeclash.cobblewand.creative;

import com.github.pokeclash.cobblewand.CobbleWand;
import com.github.pokeclash.cobblewand.item.CobbleWandItems;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CobbleWandTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(CobbleWand.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> MAIN_TAB = CREATIVE_TABS.register(
            "main_tab",
            () -> CreativeTabRegistry.create(
                    Component.translatable("creativeTab.cobblewand.tab"),
                    () -> new ItemStack(CobbleWandItems.COBBLEWAND) // Icon
            )
    );

    public static void register() {
        CREATIVE_TABS.register();
    }
}
