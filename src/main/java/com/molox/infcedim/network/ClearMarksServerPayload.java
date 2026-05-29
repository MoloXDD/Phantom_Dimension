package com.molox.infcedim.network;

import com.molox.infcedim.InfCeDim;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClearMarksServerPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClearMarksServerPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(InfCeDim.MOD_ID, "clear_marks_server"));

    public static final StreamCodec<FriendlyByteBuf, ClearMarksServerPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {},
                    buf -> new ClearMarksServerPayload()
            );

    @Override
    public CustomPacketPayload.Type<ClearMarksServerPayload> type() {
        return TYPE;
    }
}