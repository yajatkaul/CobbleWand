package com.github.pokeclash.cobblewand.item;

import com.github.pokeclash.cobblewand.CobbleWand;
import com.github.pokeclash.cobblewand.creative.CobbleWandTab;
import com.github.pokeclash.cobblewand.item.custom.CobbleWandItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class CobbleWandItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(CobbleWand.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> COBBLEWAND = registerItem(
            "cobblewand",
            () -> new CobbleWandItem(
                    new Item.Properties()
                            .stacksTo(1)
                            .arch$tab(CobbleWandTab.MAIN_TAB)
            )
    );

    private static RegistrySupplier<Item> registerItem(String name, Supplier<Item> item) {
        return ITEMS.register(name, item);
    }

    public static void register() {
        ITEMS.register();
    }
}
