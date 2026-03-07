package com.github.pokeclash.cobblewand.platform;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.github.pokeclash.cobblewand.codec.WandData;
import com.github.pokeclash.cobblewand.ui.screen.CobbleWandScreen;
import com.github.pokeclash.cobblewand.ui.screen.PokeEditScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;

import java.util.UUID;

public class CobbleWandClient {
    public static void openWandScreen(WandData data) {
        Minecraft.getInstance().setScreen(new CobbleWandScreen("Cobble Wand", data));
    }

    public static void openPokemonEditor(Pokemon pokemon, RegistryAccess registryAccess, UUID uuid) {
        Minecraft.getInstance().setScreen(new PokeEditScreen("Cobble Wand", pokemon, registryAccess, uuid));
    }
}