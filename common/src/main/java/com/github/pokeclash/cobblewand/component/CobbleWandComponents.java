package com.github.pokeclash.cobblewand.component;

import com.github.pokeclash.cobblewand.CobbleWand;
import com.github.pokeclash.cobblewand.component.utils.PokemonStorage;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;

public class CobbleWandComponents {
    private static final DeferredRegister<DataComponentType<?>> REGISTRAR =
            DeferredRegister.create(CobbleWand.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final Supplier<DataComponentType<PokemonStorage>> POKEMON_STORAGE = REGISTRAR.register(
            "pokemon_storage",
            () -> DataComponentType.<PokemonStorage>builder()
                    .persistent(PokemonStorage.CODEC)
                    .build()
    );

    public static void register() {
        REGISTRAR.register();
    }
}
