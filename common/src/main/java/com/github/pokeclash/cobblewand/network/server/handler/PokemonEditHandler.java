package com.github.pokeclash.cobblewand.network.server.handler;

import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokeball.PokeBall;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.AspectPropertyType;
import com.cobblemon.mod.common.pokemon.properties.UnaspectPropertyType;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import com.github.pokeclash.cobblewand.codec.WandData;
import com.github.pokeclash.cobblewand.item.CobbleWandItems;
import com.github.pokeclash.cobblewand.network.server.packet.PokemonEditPacket;
import com.github.pokeclash.cobblewand.permission.CobbleWandPermissionService;
import com.github.pokeclash.cobblewand.permission.CobbleWandPermissions;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class PokemonEditHandler {
    @SuppressWarnings("DataFlowIssue")
    public static void handle(PokemonEditPacket pokemonEditPacket, NetworkManager.PacketContext packetContext) {
        if (!(packetContext.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ItemStack itemStack = serverPlayer.getMainHandItem();
        if (!itemStack.is(CobbleWandItems.COBBLEWAND.get())) {
            return;
        }

        if (!CobbleWandPermissionService.hasPermission(serverPlayer, CobbleWandPermissions.POKEMON_SET)) {
            CobbleWandPermissionService.sendDeniedMessage(serverPlayer, CobbleWandPermissions.POKEMON_SET);
            return;
        }

        if (pokemonEditPacket.wandData().flags().flatMap(WandData.Flags::statue).isPresent()
                && !CobbleWandPermissionService.hasPermission(serverPlayer, CobbleWandPermissions.STATUE_SET)) {
            CobbleWandPermissionService.sendDeniedMessage(serverPlayer, CobbleWandPermissions.STATUE_SET);
            return;
        }

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
                    var aspect = AspectPropertyType.INSTANCE.fromString("is_statue");
                    if (aspect != null) {
                        aspect.apply(pokemonEntity.getPokemon());
                    }
                } else {
                    pokemonEntity.setNoAi(false);
                    UncatchableProperty.INSTANCE.catchable().apply(pokemon);
                    pokemonEntity.noPhysics = false;
                    var unaspect = UnaspectPropertyType.INSTANCE.fromString("is_statue");
                    if (unaspect != null) {
                        unaspect.apply(pokemonEntity.getPokemon());
                    }
                }
            });

            newWandData.flags().flatMap(WandData.Flags::canBattle).ifPresent((canBattle) -> {
                if (!canBattle) {
                    var unaspect = UnaspectPropertyType.INSTANCE.fromString("cant_battle");
                    if (unaspect != null) {
                        unaspect.apply(pokemonEntity.getPokemon());
                    }
                    pokemonEntity.getEntityData().set(PokemonEntity.Companion.getHIDE_LABEL(), true);
                } else {
                    var aspect = AspectPropertyType.INSTANCE.fromString("cant_battle");
                    if (aspect != null) {
                        aspect.apply(pokemonEntity.getPokemon());
                    }
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
