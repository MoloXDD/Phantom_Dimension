package com.molox.infcedim;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;

public class SwapCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("infcedim")
                        .then(Commands.literal("swap")
                                .requires(CommandSourceStack::isPlayer)
                                .executes(context -> swap(context.getSource()))
                        )
        );
    }

    private static int swap(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        ServerLevel currentLevel = player.serverLevel();
        ResourceKey<Level> currentDim = currentLevel.dimension();
        ResourceKey<Level> targetDim;

        if (currentDim.equals(Level.OVERWORLD)) {
            targetDim = InfCeDim.PHANTOM_WORLD;
        } else if (currentDim.equals(InfCeDim.PHANTOM_WORLD)) {
            targetDim = Level.OVERWORLD;
        } else {
            source.sendFailure(Component.translatable("commands.infcedim.swap.wrong_dimension"));
            return 0;
        }

        ServerLevel targetLevel = source.getServer().getLevel(targetDim);
        if (targetLevel == null) {
            source.sendFailure(Component.translatable("commands.infcedim.swap.dimension_not_found"));
            return 0;
        }

        int targetX = (int) player.getX();
        int targetZ = (int) player.getZ();

        BlockPos safePos = findSafeSurfacePos(targetLevel, targetX, targetZ);
        if (safePos == null) {
            source.sendFailure(Component.translatable("commands.infcedim.swap.no_safe_pos"));
            return 0;
        }

        player.teleportTo(targetLevel,
                safePos.getX() + 0.5,
                safePos.getY(),
                safePos.getZ() + 0.5,
                player.getYRot(),
                player.getXRot());

        source.sendSuccess(() -> Component.translatable("commands.infcedim.swap.success"), false);
        return 1;
    }

    @Nullable
    private static BlockPos findSafeSurfacePos(ServerLevel level, int centerX, int centerZ) {
        BlockPos pos = getSurfacePos(level, centerX, centerZ);
        if (pos != null) return pos;

        for (int radius = 1; radius <= 16; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) continue;
                    pos = getSurfacePos(level, centerX + dx, centerZ + dz);
                    if (pos != null) return pos;
                }
            }
        }

        return null;
    }

    @Nullable
    private static BlockPos getSurfacePos(ServerLevel level, int x, int z) {
        LevelChunk chunk = level.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));

        int motionBlockingY = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING, x & 15, z & 15);
        if (motionBlockingY < level.getMinBuildHeight()) return null;

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        mutable.set(x, motionBlockingY, z);
        if (!level.canSeeSky(mutable.above())) return null;

        if (!level.getBlockState(mutable).getFluidState().isEmpty()) {
            return mutable.above().immutable();
        }

        for (int y = motionBlockingY + 1; y >= level.getMinBuildHeight(); y--) {
            mutable.set(x, y, z);
            if (!level.getBlockState(mutable).getFluidState().isEmpty()) break;
            if (Block.isFaceFull(level.getBlockState(mutable).getCollisionShape(level, mutable), Direction.UP)) {
                return mutable.above().immutable();
            }
        }

        return null;
    }
}