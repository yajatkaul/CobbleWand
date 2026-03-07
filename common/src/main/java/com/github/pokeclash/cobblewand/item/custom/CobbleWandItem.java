package com.github.pokeclash.cobblewand.item.custom;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.properties.AspectPropertyType;
import com.cobblemon.mod.common.pokemon.properties.UncatchableProperty;
import com.github.pokeclash.cobblewand.codec.WandData;
import com.github.pokeclash.cobblewand.component.CobbleWandComponents;
import com.github.pokeclash.cobblewand.component.utils.PokemonStorage;
import com.github.pokeclash.cobblewand.ui.screen.CobbleWandScreen;
import com.github.pokeclash.cobblewand.ui.screen.PokeEditScreen;
import kotlin.Unit;
import net.minecraft.client.Minecraft;
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

        PokemonStorage storage = stack.getOrDefault(CobbleWandComponents.POKEMON_STORAGE.get(), PokemonStorage.defaultStorage());

        if (level.isClientSide) {
            Minecraft.getInstance().setScreen(new CobbleWandScreen("Cobble Wand", storage.wandData()));
        }

        return super.use(level, player, hand);
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
                        if (statue) {
                            pokemonEntity.setNoAi(true);
                            pokemonEntity.setPersistenceRequired();
                            UncatchableProperty.INSTANCE.uncatchable().apply(pokemon);
                            pokemonEntity.noPhysics = true;
                            AspectPropertyType.INSTANCE.fromString("is_statue").apply(pokemon);
                            pokemonEntity.getEntityData().set(PokemonEntity.Companion.getHIDE_LABEL(), true);
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
                Minecraft.getInstance().setScreen(new CobbleWandScreen("Cobble Wand", storge.wandData()));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        if (player.level().isClientSide) {
            if (livingEntity instanceof PokemonEntity pokemonEntity) {
                Minecraft.getInstance().setScreen(new PokeEditScreen("Cobble Wand", pokemonEntity.getPokemon(), player.registryAccess(), pokemonEntity.getUUID()));
            }
        }

        return InteractionResult.SUCCESS;
    }
}
