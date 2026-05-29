package com.molox.infcedim.network;

import com.molox.infcedim.InfCeDim;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MarkEntityPayload(int entityId, boolean mark) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MarkEntityPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(InfCeDim.MOD_ID, "mark_entity"));

    public static final StreamCodec<FriendlyByteBuf, MarkEntityPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeInt(payload.entityId);
                        buf.writeBoolean(payload.mark);
                    },
                    buf -> new MarkEntityPayload(buf.readInt(), buf.readBoolean())
            );

    @Override
    public CustomPacketPayload.Type<MarkEntityPayload> type() {
        return TYPE;
    }
}