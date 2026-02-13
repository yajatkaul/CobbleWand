package com.github.pokeclash.cobblewand.item.custom;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.github.pokeclash.cobblewand.CobbleWand;
import com.github.pokeclash.cobblewand.component.CobbleWandComponents;
import com.github.pokeclash.cobblewand.component.utils.PokemonStorage;
import com.github.pokeclash.cobblewand.ui.screen.CobbleWandScreen;
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
        ItemStack stack = player.getUseItem();
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(stack);
        }

        PokemonStorage storge = stack.getOrDefault(CobbleWandComponents.POKEMON_STORAGE.get(), PokemonStorage.defaultStorage());

        if (storge.getPokemon(player.registryAccess()) != null) {
            CobbleWand.LOGGER.info(storge.getPokemon(player.registryAccess()).showdownId());
            return InteractionResultHolder.success(stack);
        }

        if (level.isClientSide) {
            Minecraft.getInstance().setScreen(new CobbleWandScreen("cobble_wand_screen"));
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

        if (pokemon != null && !player.isCrouching()) {
            if (level instanceof ServerLevel serverLevel) {
                BlockPos pos = useOnContext.getClickedPos();
                pokemon.sendOut(serverLevel, new Vec3(pos.getX(), pos.getY(), pos.getZ()), null, (pokemonEntity -> {
                    pokemonEntity.setNoAi(true);

                    float oppositeYaw = player.getYRot() + 180.0F;
                    pokemonEntity.setYRot(oppositeYaw);
                    pokemonEntity.setYHeadRot(oppositeYaw);
                    pokemonEntity.setYBodyRot(oppositeYaw);

                    pokemonEntity.setPersistenceRequired();

                    return Unit.INSTANCE;
                }));
            }
        } else {
            if (level.isClientSide) {
                Minecraft.getInstance().setScreen(new CobbleWandScreen("cobble_wand_screen"));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        return InteractionResult.SUCCESS;
    }
}
