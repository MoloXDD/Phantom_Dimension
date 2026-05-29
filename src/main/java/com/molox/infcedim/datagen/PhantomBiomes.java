package com.molox.infcedim.datagen;

import com.molox.infcedim.InfCeDim;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PhantomBiomes {
    public static final DeferredRegister<Biome> BIOMES =
            DeferredRegister.create(Registries.BIOME, InfCeDim.MOD_ID);

    public static final Supplier<Biome> PHANTOM_BADLANDS = BIOMES.register("phantom_badlands", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_BAMBOO_JUNGLE = BIOMES.register("phantom_bamboo_jungle", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_BEACH = BIOMES.register("phantom_beach", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_BIRCH_FOREST = BIOMES.register("phantom_birch_forest", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_CHERRY_GROVE = BIOMES.register("phantom_cherry_grove", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_COLD_OCEAN = BIOMES.register("phantom_cold_ocean", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_DARK_FOREST = BIOMES.register("phantom_dark_forest", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_DEEP_COLD_OCEAN = BIOMES.register("phantom_deep_cold_ocean", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_DEEP_DARK = BIOMES.register("phantom_deep_dark", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_DEEP_FROZEN_OCEAN = BIOMES.register("phantom_deep_frozen_ocean", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_DEEP_LUKEWARM_OCEAN = BIOMES.register("phantom_deep_lukewarm_ocean", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_DEEP_OCEAN = BIOMES.register("phantom_deep_ocean", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_DESERT = BIOMES.register("phantom_desert", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_DRIPSTONE_CAVES = BIOMES.register("phantom_dripstone_caves", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_ERODED_BADLANDS = BIOMES.register("phantom_eroded_badlands", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_FLOWER_FOREST = BIOMES.register("phantom_flower_forest", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_FOREST = BIOMES.register("phantom_forest", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_FROZEN_OCEAN = BIOMES.register("phantom_frozen_ocean", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_FROZEN_PEAKS = BIOMES.register("phantom_frozen_peaks", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_FROZEN_RIVER = BIOMES.register("phantom_frozen_river", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_GROVE = BIOMES.register("phantom_grove", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_ICE_SPIKES = BIOMES.register("phantom_ice_spikes", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_JAGGED_PEAKS = BIOMES.register("phantom_jagged_peaks", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_JUNGLE = BIOMES.register("phantom_jungle", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_LUKEWARM_OCEAN = BIOMES.register("phantom_lukewarm_ocean", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_LUSH_CAVES = BIOMES.register("phantom_lush_caves", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_MANGROVE_SWAMP = BIOMES.register("phantom_mangrove_swamp", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_MEADOW = BIOMES.register("phantom_meadow", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_MUSHROOM_FIELDS = BIOMES.register("phantom_mushroom_fields", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_OCEAN = BIOMES.register("phantom_ocean", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_OLD_GROWTH_BIRCH_FOREST = BIOMES.register("phantom_old_growth_birch_forest", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_OLD_GROWTH_PINE_TAIGA = BIOMES.register("phantom_old_growth_pine_taiga", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_OLD_GROWTH_SPRUCE_TAIGA = BIOMES.register("phantom_old_growth_spruce_taiga", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_PLAINS = BIOMES.register("phantom_plains", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_RIVER = BIOMES.register("phantom_river", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_SAVANNA = BIOMES.register("phantom_savanna", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_SAVANNA_PLATEAU = BIOMES.register("phantom_savanna_plateau", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_SNOWY_BEACH = BIOMES.register("phantom_snowy_beach", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_SNOWY_PLAINS = BIOMES.register("phantom_snowy_plains", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_SNOWY_SLOPES = BIOMES.register("phantom_snowy_slopes", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_SNOWY_TAIGA = BIOMES.register("phantom_snowy_taiga", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_SPARSE_JUNGLE = BIOMES.register("phantom_sparse_jungle", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_STONY_PEAKS = BIOMES.register("phantom_stony_peaks", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_STONY_SHORE = BIOMES.register("phantom_stony_shore", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_SUNFLOWER_PLAINS = BIOMES.register("phantom_sunflower_plains", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_SWAMP = BIOMES.register("phantom_swamp", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_TAIGA = BIOMES.register("phantom_taiga", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_WARM_OCEAN = BIOMES.register("phantom_warm_ocean", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_WINDSWEPT_FOREST = BIOMES.register("phantom_windswept_forest", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_WINDSWEPT_GRAVELLY_HILLS = BIOMES.register("phantom_windswept_gravelly_hills", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_WINDSWEPT_HILLS = BIOMES.register("phantom_windswept_hills", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_WINDSWEPT_SAVANNA = BIOMES.register("phantom_windswept_savanna", PhantomBiomeFactory::makeGenericBiome);
    public static final Supplier<Biome> PHANTOM_WOODED_BADLANDS = BIOMES.register("phantom_wooded_badlands", PhantomBiomeFactory::makeGenericBiome);
}
