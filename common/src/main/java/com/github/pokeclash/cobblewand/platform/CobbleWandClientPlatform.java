package com.github.pokeclash.cobblewand.platform;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.github.pokeclash.cobblewand.codec.WandData;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.RegistryAccess;

import java.util.UUID;

public class CobbleWandClientPlatform {
    @ExpectPlatform
    public static void openWandScreen(WandData data) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void openPokemonEditor(Pokemon pokemon, RegistryAccess registryAccess, UUID uuid) {
        throw new AssertionError();
    }
}