package com.molox.infcedim.client;

import com.molox.infcedim.InfCeDim;
import com.molox.infcedim.InfCeDimConfig;
import com.molox.infcedim.ScenarioCoreItem;
import com.molox.infcedim.network.ClearMarksPayload;
import com.molox.infcedim.network.ClearMarksServerPayload;
import com.molox.infcedim.network.MarkEntityPayload;
import com.molox.infcedim.network.UnmarkEntityServerPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(modid = InfCeDim.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class InfCeDimClient {

    private static boolean wasHoldingScenarioCore = false;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    ScenarioCoreItem.SCENARIO_CORE.get(),
                    ResourceLocation.fromNamespaceAndPath(InfCeDim.MOD_ID, "in_phantom_world"),
                    (stack, level, entity, seed) -> {
                        if (level != null && level.dimension().equals(InfCeDim.PHANTOM_WORLD)) {
                            return 1.0F;
                        }
                        if (level == null && entity != null && entity.level().dimension().equals(InfCeDim.PHANTOM_WORLD)) {
                            return 1.0F;
                        }
                        return 0.0F;
                    }
            );
        });
    }

    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(InfCeDim.MOD_ID);

        registrar.playToClient(
                MarkEntityPayload.TYPE,
                MarkEntityPayload.STREAM_CODEC,
                (payload, context) -> {
                    if (payload.mark()) {
                        ScenarioCoreGlowTracker.mark(payload.entityId());
                    } else {
                        ScenarioCoreGlowTracker.unmark(payload.entityId());
                    }
                }
        );

        registrar.playToClient(
                ClearMarksPayload.TYPE,
                ClearMarksPayload.STREAM_CODEC,
                (payload, context) -> ScenarioCoreGlowTracker.clearAll()
        );

        registrar.playToServer(
                ClearMarksServerPayload.TYPE,
                ClearMarksServerPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                        ScenarioCoreItem.clearServerMarks(serverPlayer.getUUID());
                    }
                })
        );

        registrar.playToServer(
                UnmarkEntityServerPayload.TYPE,
                UnmarkEntityServerPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                        ScenarioCoreItem.removeServerMark(serverPlayer.getUUID(), payload.entityId());
                    }
                })
        );
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            if (wasHoldingScenarioCore) {
                ScenarioCoreGlowTracker.clearAll();
                PacketDistributor.sendToServer(new ClearMarksServerPayload());
                wasHoldingScenarioCore = false;
            }
            return;
        }

        ItemStack held = mc.player.getMainHandItem();
        boolean holdingScenarioCore = held.getItem() instanceof ScenarioCoreItem
                && !mc.player.getCooldowns().isOnCooldown(held.getItem());

        if (!holdingScenarioCore) {
            if (wasHoldingScenarioCore) {
                ScenarioCoreGlowTracker.clearAll();
                PacketDistributor.sendToServer(new ClearMarksServerPayload());
            }
            wasHoldingScenarioCore = false;
            return;
        }

        wasHoldingScenarioCore = true;

        double maxDist = InfCeDimConfig.MARK_RADIUS.get();
        double maxDistSq = maxDist * maxDist;

        List<Integer> toUnmark = new ArrayList<>();
        Iterator<Integer> it = ScenarioCoreGlowTracker.getMarked().iterator();
        while (it.hasNext()) {
            int entityId = it.next();
            Entity entity = mc.level.getEntity(entityId);
            if (entity == null || !entity.isAlive()) {
                it.remove();
                toUnmark.add(entityId);
                continue;
            }
            if (mc.player.distanceToSqr(entity) > maxDistSq) {
                it.remove();
                toUnmark.add(entityId);
            }
        }

        for (int entityId : toUnmark) {
            PacketDistributor.sendToServer(new UnmarkEntityServerPayload(entityId));
        }
    }
}