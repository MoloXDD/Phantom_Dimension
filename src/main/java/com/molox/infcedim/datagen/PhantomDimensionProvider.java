package com.molox.infcedim.datagen;

import com.molox.infcedim.InfCeDim;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PhantomDimensionProvider extends DatapackBuiltinEntriesProvider {

    public static final ResourceKey<LevelStem> PHANTOM_WORLD_STEM = ResourceKey.create(
            Registries.LEVEL_STEM,
            ResourceLocation.fromNamespaceAndPath(InfCeDim.MOD_ID, "phantom_world")
    );

    private static final Map<ResourceKey<Biome>, ResourceKey<Biome>> VANILLA_TO_PHANTOM = buildMap();

    private static Map<ResourceKey<Biome>, ResourceKey<Biome>> buildMap() {
        Map<ResourceKey<Biome>, ResourceKey<Biome>> map = new HashMap<>();
        map.put(Biomes.BADLANDS,                 pk("phantom_badlands"));
        map.put(Biomes.BAMBOO_JUNGLE,            pk("phantom_bamboo_jungle"));
        map.put(Biomes.BEACH,                    pk("phantom_beach"));
        map.put(Biomes.BIRCH_FOREST,             pk("phantom_birch_forest"));
        map.put(Biomes.CHERRY_GROVE,             pk("phantom_cherry_grove"));
        map.put(Biomes.COLD_OCEAN,               pk("phantom_cold_ocean"));
        map.put(Biomes.DARK_FOREST,              pk("phantom_dark_forest"));
        map.put(Biomes.DEEP_COLD_OCEAN,          pk("phantom_deep_cold_ocean"));
        map.put(Biomes.DEEP_DARK,                pk("phantom_deep_dark"));
        map.put(Biomes.DEEP_FROZEN_OCEAN,        pk("phantom_deep_frozen_ocean"));
        map.put(Biomes.DEEP_LUKEWARM_OCEAN,      pk("phantom_deep_lukewarm_ocean"));
        map.put(Biomes.DEEP_OCEAN,               pk("phantom_deep_ocean"));
        map.put(Biomes.DESERT,                   pk("phantom_desert"));
        map.put(Biomes.DRIPSTONE_CAVES,          pk("phantom_dripstone_caves"));
        map.put(Biomes.ERODED_BADLANDS,          pk("phantom_eroded_badlands"));
        map.put(Biomes.FLOWER_FOREST,            pk("phantom_flower_forest"));
        map.put(Biomes.FOREST,                   pk("phantom_forest"));
        map.put(Biomes.FROZEN_OCEAN,             pk("phantom_frozen_ocean"));
        map.put(Biomes.FROZEN_PEAKS,             pk("phantom_frozen_peaks"));
        map.put(Biomes.FROZEN_RIVER,             pk("phantom_frozen_river"));
        map.put(Biomes.GROVE,                    pk("phantom_grove"));
        map.put(Biomes.ICE_SPIKES,               pk("phantom_ice_spikes"));
        map.put(Biomes.JAGGED_PEAKS,             pk("phantom_jagged_peaks"));
        map.put(Biomes.JUNGLE,                   pk("phantom_jungle"));
        map.put(Biomes.LUKEWARM_OCEAN,           pk("phantom_lukewarm_ocean"));
        map.put(Biomes.LUSH_CAVES,               pk("phantom_lush_caves"));
        map.put(Biomes.MANGROVE_SWAMP,           pk("phantom_mangrove_swamp"));
        map.put(Biomes.MEADOW,                   pk("phantom_meadow"));
        map.put(Biomes.MUSHROOM_FIELDS,          pk("phantom_mushroom_fields"));
        map.put(Biomes.OCEAN,                    pk("phantom_ocean"));
        map.put(Biomes.OLD_GROWTH_BIRCH_FOREST,  pk("phantom_old_growth_birch_forest"));
        map.put(Biomes.OLD_GROWTH_PINE_TAIGA,    pk("phantom_old_growth_pine_taiga"));
        map.put(Biomes.OLD_GROWTH_SPRUCE_TAIGA,  pk("phantom_old_growth_spruce_taiga"));
        map.put(Biomes.PLAINS,                   pk("phantom_plains"));
        map.put(Biomes.RIVER,                    pk("phantom_river"));
        map.put(Biomes.SAVANNA,                  pk("phantom_savanna"));
        map.put(Biomes.SAVANNA_PLATEAU,          pk("phantom_savanna_plateau"));
        map.put(Biomes.SNOWY_BEACH,              pk("phantom_snowy_beach"));
        map.put(Biomes.SNOWY_PLAINS,             pk("phantom_snowy_plains"));
        map.put(Biomes.SNOWY_SLOPES,             pk("phantom_snowy_slopes"));
        map.put(Biomes.SNOWY_TAIGA,              pk("phantom_snowy_taiga"));
        map.put(Biomes.SPARSE_JUNGLE,            pk("phantom_sparse_jungle"));
        map.put(Biomes.STONY_PEAKS,              pk("phantom_stony_peaks"));
        map.put(Biomes.STONY_SHORE,              pk("phantom_stony_shore"));
        map.put(Biomes.SUNFLOWER_PLAINS,         pk("phantom_sunflower_plains"));
        map.put(Biomes.SWAMP,                    pk("phantom_swamp"));
        map.put(Biomes.TAIGA,                    pk("phantom_taiga"));
        map.put(Biomes.WARM_OCEAN,               pk("phantom_warm_ocean"));
        map.put(Biomes.WINDSWEPT_FOREST,         pk("phantom_windswept_forest"));
        map.put(Biomes.WINDSWEPT_GRAVELLY_HILLS, pk("phantom_windswept_gravelly_hills"));
        map.put(Biomes.WINDSWEPT_HILLS,          pk("phantom_windswept_hills"));
        map.put(Biomes.WINDSWEPT_SAVANNA,        pk("phantom_windswept_savanna"));
        map.put(Biomes.WOODED_BADLANDS,          pk("phantom_wooded_badlands"));
        return map;
    }

    private static ResourceKey<Biome> pk(String name) {
        return ResourceKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath(InfCeDim.MOD_ID, name));
    }

    @SuppressWarnings("unchecked")
    private static void addBiomesViaReflection(OverworldBiomeBuilder builder,
            Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer) {
        try {
            Method m = OverworldBiomeBuilder.class.getDeclaredMethod("addBiomes", Consumer.class);
            m.setAccessible(true);
            m.invoke(builder, consumer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke OverworldBiomeBuilder.addBiomes via reflection", e);
        }
    }

    public PhantomDimensionProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, makeBuilder(), Set.of(InfCeDim.MOD_ID));
    }

    private static RegistrySetBuilder makeBuilder() {
        return new RegistrySetBuilder()
                .add(Registries.LEVEL_STEM, ctx -> {
                    var dimensionTypes = ctx.lookup(Registries.DIMENSION_TYPE);
                    var noiseSettings  = ctx.lookup(Registries.NOISE_SETTINGS);
                    var biomes         = ctx.lookup(Registries.BIOME);

                    List<Pair<Climate.ParameterPoint, net.minecraft.core.Holder<Biome>>> biomeList = new ArrayList<>();

                    addBiomesViaReflection(new OverworldBiomeBuilder(), pair -> {
                        ResourceKey<Biome> vanillaKey = pair.getSecond();
                        ResourceKey<Biome> phantomKey = VANILLA_TO_PHANTOM.get(vanillaKey);
                        ResourceKey<Biome> resolved   = phantomKey != null ? phantomKey : vanillaKey;
                        biomeList.add(Pair.of(pair.getFirst(), biomes.getOrThrow(resolved)));
                    });

                    var biomeSource = MultiNoiseBiomeSource.createFromList(
                            new Climate.ParameterList<>(biomeList));
                    var chunkGenerator = new NoiseBasedChunkGenerator(
                            biomeSource,
                            noiseSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD));

                    ctx.register(PHANTOM_WORLD_STEM, new LevelStem(
                            dimensionTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD),
                            chunkGenerator));
                });
    }

    @Override
    public String getName() {
        return "Phantom World Dimension";
    }
}
