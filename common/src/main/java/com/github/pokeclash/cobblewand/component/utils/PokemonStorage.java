package com.github.pokeclash.cobblewand.component.utils;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.github.pokeclash.cobblewand.codec.WandData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public record PokemonStorage(
        CompoundTag tag,
        WandData wandData
) {
    public static final Codec<PokemonStorage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CompoundTag.CODEC.fieldOf("tag").forGetter(PokemonStorage::tag),
            WandData.CODEC.fieldOf("wand_data").forGetter(PokemonStorage::wandData)
    ).apply(instance, PokemonStorage::new));

    public static PokemonStorage defaultStorage() {
        return new PokemonStorage(
                new CompoundTag(),
                new WandData(Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty(),Optional.empty())
        );
    }

    public Pokemon getPokemon(RegistryAccess registryAccess) {
        if (tag.isEmpty()) {
            return null;
        }
        return new Pokemon().loadFromNBT(registryAccess, tag);
    }

    public PokemonStorage save(RegistryAccess registryAccess, Pokemon pokemon) {
        CompoundTag storgeTag = pokemon.saveToNBT(registryAccess, new CompoundTag());
        return new PokemonStorage(storgeTag, this.wandData);
    }
}