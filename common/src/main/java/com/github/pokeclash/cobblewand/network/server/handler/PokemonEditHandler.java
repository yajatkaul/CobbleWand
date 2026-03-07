package com.github.pokeclash.cobblewand.network.server.handler;

import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokeball.PokeBall;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.AspectPropertyType;
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
                    pokemonEntity.getEntityData().set(PokemonEntity.Companion.getHIDE_LABEL(), true);
                }
            });

            float oppositeYaw = packetContext.getPlayer().getYRot() + 180.0F;
            pokemonEntity.setYRot(oppositeYaw);
            pokemonEntity.setYHeadRot(oppositeYaw);
            pokemonEntity.setYBodyRot(oppositeYaw);
        }
    }
}
