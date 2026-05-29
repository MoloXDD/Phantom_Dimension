package com.molox.infcedim;

import com.molox.infcedim.network.ClearMarksPayload;
import com.molox.infcedim.network.MarkEntityPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ScenarioCoreItem extends Item {

    public static final String COOLDOWN_NBT_KEY = "inf_ce_dim:scenario_core_last_use";

    private static final ConcurrentHashMap<UUID, Set<Integer>> serverMarks = new ConcurrentHashMap<>();

    public static DeferredItem<ScenarioCoreItem> SCENARIO_CORE;

    public static void register() {
        SCENARIO_CORE = InfCeDim.ITEMS.registerItem(
                "scenario_core",
                ScenarioCoreItem::new,
                new Item.Properties().stacksTo(1)
        );
    }

    public ScenarioCoreItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (player.level().isClientSide()) return InteractionResult.PASS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;
        if (serverPlayer.getCooldowns().isOnCooldown(stack.getItem())) return InteractionResult.PASS;
        if (target == player) return InteractionResult.PASS;
        if (player.isCrouching()) return InteractionResult.PASS;

        double maxDist = InfCeDimConfig.MARK_RADIUS.get();
        if (serverPlayer.distanceToSqr(target) > maxDist * maxDist) return InteractionResult.PASS;

        String entityId = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).toString();
        if (!InfCeDimConfig.isEntityAllowed(entityId)) {
            return InteractionResult.PASS;
        }

        UUID playerId = serverPlayer.getUUID();
        Set<Integer> marks = serverMarks.computeIfAbsent(playerId, k -> new HashSet<>());

        if (marks.contains(target.getId())) {
            marks.remove(target.getId());
            PacketDistributor.sendToPlayer(serverPlayer, new MarkEntityPayload(target.getId(), false));
            serverPlayer.sendSystemMessage(Component.translatable("item.inf_ce_dim.scenario_core.unmarked"));
        } else {
            marks.add(target.getId());
            PacketDistributor.sendToPlayer(serverPlayer, new MarkEntityPayload(target.getId(), true));
            serverPlayer.sendSystemMessage(Component.translatable("item.inf_ce_dim.scenario_core.marked"));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.isCrouching()) {
            return InteractionResultHolder.pass(stack);
        }

        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;
        ServerLevel currentLevel = serverPlayer.serverLevel();

        long cooldownMs = (long) InfCeDimConfig.SCENARIO_CORE_COOLDOWN_SECONDS.get() * 1000L;
        long now = System.currentTimeMillis();
        long lastUse = serverPlayer.getPersistentData().getLong(COOLDOWN_NBT_KEY);

        if (now - lastUse < cooldownMs) {
            return InteractionResultHolder.fail(stack);
        }

        net.minecraft.resources.ResourceKey<Level> currentDim = currentLevel.dimension();
        net.minecraft.resources.ResourceKey<Level> targetDim;

        if (currentDim.equals(Level.OVERWORLD)) {
            targetDim = InfCeDim.PHANTOM_WORLD;
        } else if (currentDim.equals(InfCeDim.PHANTOM_WORLD)) {
            targetDim = Level.OVERWORLD;
        } else {
            serverPlayer.sendSystemMessage(Component.translatable("commands.infcedim.swap.wrong_dimension"));
            return InteractionResultHolder.fail(stack);
        }

        ServerLevel targetLevel = serverPlayer.getServer().getLevel(targetDim);
        if (targetLevel == null) {
            serverPlayer.sendSystemMessage(Component.translatable("commands.infcedim.swap.dimension_not_found"));
            return InteractionResultHolder.fail(stack);
        }

        int targetX = (int) serverPlayer.getX();
        int targetZ = (int) serverPlayer.getZ();

        BlockPos playerSafePos = findSafeSurfacePos(targetLevel, targetX, targetZ);
        if (playerSafePos == null) {
            serverPlayer.sendSystemMessage(Component.translatable("commands.infcedim.swap.no_safe_pos"));
            return InteractionResultHolder.fail(stack);
        }

        UUID playerId = serverPlayer.getUUID();
        Set<Integer> marks = serverMarks.getOrDefault(playerId, new HashSet<>());

        List<Entity> toTeleport = new ArrayList<>();
        for (int entityId : marks) {
            Entity entity = currentLevel.getEntity(entityId);
            if (entity != null && entity.isAlive()) {
                toTeleport.add(entity);
            }
        }

        List<BlockPos> entityDestinations = new ArrayList<>();
        for (Entity entity : toTeleport) {
            BlockPos candidatePos = findEntitySurfacePos(targetLevel, entity, (int) entity.getX(), (int) entity.getZ());
            if (candidatePos == null) {
                serverPlayer.sendSystemMessage(Component.translatable("commands.infcedim.swap.entity_no_safe_pos"));
                return InteractionResultHolder.fail(stack);
            }
            entityDestinations.add(candidatePos);
        }

        serverMarks.remove(playerId);
        PacketDistributor.sendToPlayer(serverPlayer, new ClearMarksPayload());

        for (int i = 0; i < toTeleport.size(); i++) {
            Entity entity = toTeleport.get(i);
            BlockPos dest = entityDestinations.get(i);
            entity.teleportTo(targetLevel,
                    dest.getX() + 0.5,
                    dest.getY(),
                    dest.getZ() + 0.5,
                    java.util.Set.of(),
                    entity.getYRot(),
                    entity.getXRot());
        }

        serverPlayer.getPersistentData().putLong(COOLDOWN_NBT_KEY, now);

        int cooldownTicks = InfCeDimConfig.SCENARIO_CORE_COOLDOWN_SECONDS.get() * 20;
        serverPlayer.getCooldowns().addCooldown(stack.getItem(), cooldownTicks);

        serverPlayer.teleportTo(targetLevel,
                playerSafePos.getX() + 0.5,
                playerSafePos.getY(),
                playerSafePos.getZ() + 0.5,
                serverPlayer.getYRot(),
                serverPlayer.getXRot());

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.inf_ce_dim.scenario_core.tooltip_line1"));
        tooltipComponents.add(Component.translatable("item.inf_ce_dim.scenario_core.tooltip_line2"));
    }

    public static void reapplyCooldownAfterDimensionChange(ServerPlayer serverPlayer) {
        if (SCENARIO_CORE == null) return;
        Item item = SCENARIO_CORE.get();

        long lastUse = serverPlayer.getPersistentData().getLong(COOLDOWN_NBT_KEY);
        if (lastUse == 0) return;

        long cooldownMs = (long) InfCeDimConfig.SCENARIO_CORE_COOLDOWN_SECONDS.get() * 1000L;
        long elapsed = System.currentTimeMillis() - lastUse;
        long remainingMs = cooldownMs - elapsed;

        if (remainingMs <= 0) {
            serverPlayer.getCooldowns().removeCooldown(item);
            return;
        }

        int remainingTicks = (int) (remainingMs / 50L);
        serverPlayer.getCooldowns().addCooldown(item, remainingTicks);
    }

    public static void clearServerMarks(UUID playerId) {
        serverMarks.remove(playerId);
    }

    public static void removeServerMark(UUID playerId, int entityId) {
        Set<Integer> marks = serverMarks.get(playerId);
        if (marks != null) {
            marks.remove(entityId);
        }
    }

    @Nullable
    private static BlockPos findEntitySurfacePos(ServerLevel level, Entity entity, int centerX, int centerZ) {
        BlockPos pos = getEntitySurfacePos(level, entity, centerX, centerZ);
        if (pos != null) return pos;

        for (int radius = 1; radius <= 16; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) continue;
                    pos = getEntitySurfacePos(level, entity, centerX + dx, centerZ + dz);
                    if (pos != null) return pos;
                }
            }
        }

        return null;
    }

    @Nullable
    private static BlockPos getEntitySurfacePos(ServerLevel level, Entity entity, int x, int z) {
        LevelChunk chunk = level.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));

        int motionBlockingY = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING, x & 15, z & 15);
        if (motionBlockingY < level.getMinBuildHeight()) return null;

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int y = motionBlockingY + 1; y >= level.getMinBuildHeight(); y--) {
            mutable.set(x, y, z);
            if (!level.getBlockState(mutable).getFluidState().isEmpty()) break;
            if (Block.isFaceFull(level.getBlockState(mutable).getCollisionShape(level, mutable), Direction.UP)) {
                BlockPos candidate = mutable.above().immutable();
                BlockPos result = findEntitySafePos(level, entity, candidate);
                if (result != null) return result;
                break;
            }
        }

        return null;
    }

    @Nullable
    private static BlockPos findEntitySafePos(ServerLevel level, Entity entity, BlockPos surfacePos) {
        AABB entityBox = entity.getBoundingBox();
        double halfWidth = entityBox.getXsize() / 2.0;
        double height = entityBox.getYsize();

        for (int dy = 0; dy <= 10; dy++) {
            BlockPos candidate = surfacePos.above(dy);
            AABB testBox = new AABB(
                    candidate.getX() + 0.5 - halfWidth,
                    candidate.getY(),
                    candidate.getZ() + 0.5 - halfWidth,
                    candidate.getX() + 0.5 + halfWidth,
                    candidate.getY() + height,
                    candidate.getZ() + 0.5 + halfWidth
            ).deflate(1.0E-7);
            if (level.noCollision(entity, testBox)) {
                return candidate;
            }
        }

        return null;
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