package com.github.pokeclash.cobblewand.network.server.handler;

import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokeball.PokeBall;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.AspectPropertyType;
import com.cobblemon.mod.common.pokemon.properties.UnaspectPropertyType;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import com.github.pokeclash.cobblewand.codec.WandData;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonEditPacket;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class PokemonEditHandler {
    public static void handle(PokemonEditPacket pokemonEditPacket, NetworkManager.PacketContext packetContext) {
        Pokemon pokemon = pokemonEditPacket.pokemon();
        WandData newWandData = pokemonEditPacket.wandData();

        ServerLevel level = (ServerLevel) packetContext.getPlayer().level();
        Entity entity = level.getEntity(pokemonEditPacket.uuid());

        if (pokemon.isNPCOwned() || pokemon.isPlayerOwned()) {
            if (newWandData.flags().isPresent()) {
                ResourceLocation resourceLocation = ResourceLocation.tryParse("cobblemon:" + newWandData.flags().get());
                if (resourceLocation != null) {
                    PokeBall pokeBall = PokeBalls.getPokeBall(resourceLocation);
                    if (pokeBall != null) {
                        pokemon.setCaughtBall(pokeBall);
                    }
                }
            }
        }

        if (entity instanceof PokemonEntity pokemonEntity) {
            newWandData.flags().flatMap(WandData.Flags::statue).ifPresent((statue) -> {
                if (statue) {
                    pokemonEntity.setNoAi(true);
                    pokemonEntity.setPersistenceRequired();
                    UncatchableProperty.INSTANCE.uncatchable().apply(pokemon);
                    pokemonEntity.noPhysics = true;
                    AspectPropertyType.INSTANCE.fromString("is_statue").apply(pokemonEntity.getPokemon());
                } else {
                    pokemonEntity.setNoAi(false);
                    UncatchableProperty.INSTANCE.catchable().apply(pokemon);
                    pokemonEntity.noPhysics = false;
                    UnaspectPropertyType.INSTANCE.fromString("is_statue").apply(pokemonEntity.getPokemon());
                }
            });

            newWandData.flags().flatMap(WandData.Flags::canBattle).ifPresent((canBattle) -> {
                if (!canBattle) {
                    UnaspectPropertyType.INSTANCE.fromString("cant_battle").apply(pokemonEntity.getPokemon());
                    pokemonEntity.getEntityData().set(PokemonEntity.Companion.getHIDE_LABEL(), true);
                } else {
                    AspectPropertyType.INSTANCE.fromString("cant_battle").apply(pokemonEntity.getPokemon());
                    pokemonEntity.getEntityData().set(PokemonEntity.Companion.getHIDE_LABEL(), false);
                }
            });

            newWandData.flags().flatMap(WandData.Flags::canCatch).ifPresent((canCatch) -> {
                if (!canCatch) {
                    UncatchableProperty.INSTANCE.uncatchable().apply(pokemonEntity);
                } else {
                    UncatchableProperty.INSTANCE.catchable().apply(pokemonEntity);
                }
            });

            float oppositeYaw = packetContext.getPlayer().getYRot() + 180.0F;
            pokemonEntity.setYRot(oppositeYaw);
            pokemonEntity.setYHeadRot(oppositeYaw);
            pokemonEntity.setYBodyRot(oppositeYaw);
        }
    }
}
