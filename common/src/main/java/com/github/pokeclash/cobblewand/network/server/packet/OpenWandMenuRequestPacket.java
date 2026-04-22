package com.github.pokeclash.cobblewand.network.server.packet;

import com.github.pokeclash.cobblewand.CobbleWand;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record OpenWandMenuRequestPacket() implements CustomPacketPayload {
    public static final ResourceLocation PACKET_ID = ResourceLocation.fromNamespaceAndPath(CobbleWand.MOD_ID, "open_wand_menu_request");
    public static final Type<OpenWandMenuRequestPacket> TYPE = new Type<>(PACKET_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenWandMenuRequestPacket> STREAM_CODEC = StreamCodec.unit(new OpenWandMenuRequestPacket());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
