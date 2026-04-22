package com.github.pokeclash.cobblewand.network.server.packet;

import com.github.pokeclash.cobblewand.CobbleWand;
import com.github.pokeclash.cobblewand.codec.WandData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record OpenWandMenuPacket(WandData wandData) implements CustomPacketPayload {
    public static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath(CobbleWand.MOD_ID, "open_wand_menu");
    public static final Type<OpenWandMenuPacket> TYPE = new Type<>(PACKET_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenWandMenuPacket> STREAM_CODEC = StreamCodec.composite(
            WandData.STREAM_CODEC, OpenWandMenuPacket::wandData,
            OpenWandMenuPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
