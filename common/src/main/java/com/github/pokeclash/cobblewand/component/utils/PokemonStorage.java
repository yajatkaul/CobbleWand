package com.github.pokeclash.cobblewand.component.utils;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;

public record PokemonStorage(
        CompoundTag tag
) {
    public static final Codec<PokemonStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CompoundTag.CODEC.fieldOf("tag").forGetter(PokemonStorage::tag)
    ).apply(instance, PokemonStorage::new));

    public static PokemonStorage defaultStorage() {
        return new PokemonStorage(new CompoundTag());
    }

    public Pokemon getPokemon(RegistryAccess registryAccess) {
        if (tag.isEmpty()) {
            return null;
        }
        return new Pokemon().loadFromNBT(registryAccess, tag);
    }

    public PokemonStorage save(RegistryAccess registryAccess, Pokemon pokemon) {
        CompoundTag storgeTag = pokemon.saveToNBT(registryAccess, new CompoundTag());
        return new PokemonStorage(storgeTag);
    }
}