package com.github.pokeclash.cobblewand.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.github.pokeclash.cobblewand.item.CobbleWandItems;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PokemonEntity.class, priority = 500)
public class PokemonEntityMixin {
    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void makeBossUnKillable(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        PokemonEntity pokemon = (PokemonEntity) (Object) this;

        if (damageSource.getWeaponItem() != null && damageSource.getWeaponItem().is(CobbleWandItems.COBBLEWAND.get())) {
            pokemon.discard();
        }

        if (pokemon.getAspects().contains("is_statue")) {
            cir.setReturnValue(true);
        }
    }
}
