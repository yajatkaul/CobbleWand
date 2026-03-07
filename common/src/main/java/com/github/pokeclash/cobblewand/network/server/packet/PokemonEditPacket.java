package com.github.pokeclash.cobblewand.network.server.packet;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.github.pokeclash.cobblewand.CobbleWand;
import com.github.pokeclash.cobblewand.codec.WandData;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record PokemonEditPacket(Pokemon pokemon, WandData wandData, UUID uuid) implements CustomPacketPayload {
    public static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath(CobbleWand.MOD_ID, "pokemon_edit");
    public static final Type<PokemonEditPacket> TYPE = new Type<>(PACKET_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PokemonEditPacket> STREAM_CODEC = StreamCodec.composite(
            Pokemon.getS2C_CODEC(), PokemonEditPacket::pokemon,
            WandData.STREAM_CODEC, PokemonEditPacket::wandData,
            UUIDUtil.STREAM_CODEC, PokemonEditPacket::uuid,
            PokemonEditPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
