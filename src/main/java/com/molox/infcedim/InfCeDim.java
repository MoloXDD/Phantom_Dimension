package com.molox.infcedim;

import com.molox.infcedim.datagen.PhantomBiomes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(InfCeDim.MOD_ID)
public class InfCeDim {
    public static final String MOD_ID = "inf_ce_dim";

    public static final ResourceKey<Level> PHANTOM_WORLD = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "phantom_world")
    );

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MOD_ID);

    public InfCeDim(IEventBus modEventBus, ModContainer modContainer) {
        PhantomBiomes.BIOMES.register(modEventBus);
        ITEMS.register(modEventBus);
        ScenarioCoreItem.register();
        modContainer.registerConfig(ModConfig.Type.COMMON, InfCeDimConfig.SPEC);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(this::onPlayerChangedDimension);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        SwapCommand.register(event.getDispatcher());
    }

    private void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            ScenarioCoreItem.clearServerMarks(serverPlayer.getUUID());
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(serverPlayer, new com.molox.infcedim.network.ClearMarksPayload());
            ScenarioCoreItem.reapplyCooldownAfterDimensionChange(serverPlayer);
        }
    }
}