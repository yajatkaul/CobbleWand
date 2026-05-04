package com.github.pokeclash.cobblewand.item.custom;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.AspectPropertyType;
import com.cobblemon.mod.common.pokemon.properties.UnaspectPropertyType;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import com.github.pokeclash.cobblewand.codec.WandData;
import com.github.pokeclash.cobblewand.component.CobbleWandComponents;
import com.github.pokeclash.cobblewand.component.utils.PokemonStorage;
import com.github.pokeclash.cobblewand.network.bridge.CobbleWandNetworkBridge;
import com.github.pokeclash.cobblewand.network.server.packet.OpenWandMenuRequestPacket;
import com.github.pokeclash.cobblewand.platform.CobbleWandClientPlatform;
import com.github.pokeclash.cobblewand.permission.CobbleWandPermissionService;
import com.github.pokeclash.cobblewand.permission.CobbleWandPermissions;
import kotlin.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class CobbleWandItem extends Item {
    public CobbleWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(stack);
        }

        if (level.isClientSide) {
            openWandScreenLocal(stack);
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext useOnContext) {
        ItemStack stack = useOnContext.getItemInHand();
        PokemonStorage storge = stack.getOrDefault(CobbleWandComponents.POKEMON_STORAGE.get(), PokemonStorage.defaultStorage());
        Level level = useOnContext.getLevel();
        Pokemon pokemon = storge.getPokemon(level.registryAccess());
        Player player = useOnContext.getPlayer();

        if (player != null && pokemon != null && !player.isCrouching()) {
            if (level instanceof ServerLevel serverLevel) {
                BlockPos pos = useOnContext.getClickedPos();
                pokemon.sendOut(serverLevel, new Vec3(pos.getX(), pos.getY(), pos.getZ()), null, (pokemonEntity -> {
                    storge.wandData().flags().flatMap(WandData.Flags::statue).ifPresent((statue) -> {
                        if (statue
                                && player instanceof ServerPlayer serverPlayer
                                && !CobbleWandPermissionService.hasPermission(serverPlayer, CobbleWandPermissions.STATUE_SET)) {
                            CobbleWandPermissionService.sendDeniedMessage(serverPlayer, CobbleWandPermissions.STATUE_SET);
                            return;
                        }

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

                    storge.wandData().flags().flatMap(WandData.Flags::canBattle).ifPresent((canBattle) -> {
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

                    storge.wandData().flags().flatMap(WandData.Flags::canCatch).ifPresent((canCatch) -> {
                        if (!canCatch) {
                            UncatchableProperty.INSTANCE.uncatchable().apply(pokemonEntity);
                        } else {
                            UncatchableProperty.INSTANCE.catchable().apply(pokemonEntity);
                        }
                    });

                    float oppositeYaw = player.getYRot() + 180.0F;
                    pokemonEntity.setYRot(oppositeYaw);
                    pokemonEntity.setYHeadRot(oppositeYaw);
                    pokemonEntity.setYBodyRot(oppositeYaw);

                    return Unit.INSTANCE;
                }));
            }
        } else {
            if (level.isClientSide) {
                openWandScreenLocal(stack);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private static void openWandScreenLocal(ItemStack stack) {
        PokemonStorage storage = stack.getOrDefault(CobbleWandComponents.POKEMON_STORAGE.get(), PokemonStorage.defaultStorage());
        CobbleWandClientPlatform.openWandScreen(storage.wandData());
        // Keep this C2S ping for servers that still support menu-open permission feedback.
        CobbleWandNetworkBridge.sendToServer(new OpenWandMenuRequestPacket());
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        if (player.level().isClientSide) {
            if (livingEntity instanceof PokemonEntity pokemonEntity) {
                CobbleWandClientPlatform.openPokemonEditor(
                        pokemonEntity.getPokemon(),
                        player.registryAccess(),
                        pokemonEntity.getUUID()
                );
            }
        }

        return InteractionResult.SUCCESS;
    }
}