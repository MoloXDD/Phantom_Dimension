package com.molox.infcedim.network;

import com.molox.infcedim.InfCeDim;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClearMarksPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClearMarksPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(InfCeDim.MOD_ID, "clear_marks"));

    public static final StreamCodec<FriendlyByteBuf, ClearMarksPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {},
                    buf -> new ClearMarksPayload()
            );

    @Override
    public CustomPacketPayload.Type<ClearMarksPayload> type() {
        return TYPE;
    }
}