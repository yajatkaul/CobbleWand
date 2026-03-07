package com.github.pokeclash.cobblewand.platform.neoforge;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.github.pokeclash.cobblewand.platform.CobbleWandClient;
import com.github.pokeclash.cobblewand.codec.WandData;
import net.minecraft.core.RegistryAccess;

import java.util.UUID;

public class CobbleWandClientPlatformImpl {
    public static void openWandScreen(WandData data) {
        CobbleWandClient.openWandScreen(data);
    }

    public static void openPokemonEditor(Pokemon pokemon, RegistryAccess registryAccess, UUID uuid) {
        CobbleWandClient.openPokemonEditor(pokemon, registryAccess, uuid);
    }
}