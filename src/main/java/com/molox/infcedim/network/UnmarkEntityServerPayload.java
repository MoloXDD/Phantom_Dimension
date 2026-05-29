package com.molox.infcedim.network;

import com.molox.infcedim.InfCeDim;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UnmarkEntityServerPayload(int entityId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UnmarkEntityServerPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(InfCeDim.MOD_ID, "unmark_entity_server"));

    public static final StreamCodec<FriendlyByteBuf, UnmarkEntityServerPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeInt(payload.entityId()),
                    buf -> new UnmarkEntityServerPayload(buf.readInt())
            );

    @Override
    public CustomPacketPayload.Type<UnmarkEntityServerPayload> type() {
        return TYPE;
    }
}