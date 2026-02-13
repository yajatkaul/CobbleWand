package com.github.pokeclash.cobblewand.network.server.packet;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.github.pokeclash.cobblewand.CobbleWand;
import com.github.pokeclash.cobblewand.component.utils.PokemonStorage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record PokemonSetPacket(Pokemon pokemon) implements CustomPacketPayload {
    public static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath(CobbleWand.MOD_ID, "pokemon_set");
    public static final Type<PokemonSetPacket> TYPE = new Type<>(PACKET_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PokemonSetPacket> STREAM_CODEC = StreamCodec.composite(
            Pokemon.getS2C_CODEC(), PokemonSetPacket::pokemon,
            PokemonSetPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
