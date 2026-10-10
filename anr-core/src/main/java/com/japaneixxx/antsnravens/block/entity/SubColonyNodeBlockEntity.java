package com.japaneixxx.antsnravens.block.entity;

import com.japaneixxx.antsnravens.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.Container;
import com.japaneixxx.antsnravens.world.inventory.CompositeContainer;
import com.japaneixxx.antsnravens.init.ModParticles;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public final class SubColonyNodeBlockEntity extends BlockEntity {
    private static final String QUEEN_CORE_POS_TAG = "queen_core_pos";
    private static final String LINKED_INVENTORIES_TAG = "linked_inventories";
    private static final int SCAN_INTERVAL = 20;
    private static final int INVENTORY_LINK_RANGE = 5;
    private BlockPos queenCorePos;
    private final List<BlockPos> adjacentInventories = new ArrayList<>();
    private int adjacentSlotCount;

    public SubColonyNodeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SUB_COLONY_NODE_BE.get(), pos, state);
    }

    public BlockPos getQueenCorePos() {
        return queenCorePos;
    }

    public void setQueenCorePos(BlockPos queenCorePos) {
        this.queenCorePos = queenCorePos == null ? null : queenCorePos.immutable();
        setChanged();
    }

    public void setQueenCore(BlockPos queenCorePos) {
        setQueenCorePos(queenCorePos);
    }

    public boolean linkInventory(BlockPos inventoryPos) {
        if (level == null || !isWithinLinkRange(inventoryPos)
                || !(level.getBlockEntity(inventoryPos) instanceof Container)) {
            return false;
        }
        linkSingleInventory(inventoryPos);

        if (level.getBlockState(inventoryPos).getBlock() instanceof ChestBlock) {
            for (Direction direction : Direction.values()) {
                BlockPos pairedPos = inventoryPos.relative(direction);
                if (level.getBlockState(pairedPos).getBlock() == level.getBlockState(inventoryPos).getBlock()
                        && isWithinLinkRange(pairedPos)
                        && level.getBlockEntity(pairedPos) instanceof Container) {
                    linkSingleInventory(pairedPos);
                }
            }
        }
        recalculateInventorySlots();
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        return true;
    }

    private void linkSingleInventory(BlockPos inventoryPos) {
        BlockPos immutablePos = inventoryPos.immutable();
        if (!adjacentInventories.contains(immutablePos)) {
            adjacentInventories.add(immutablePos);
        }
    }

    public boolean isWithinLinkRange(BlockPos targetPos) {
        return targetPos.distSqr(worldPosition) <= INVENTORY_LINK_RANGE * INVENTORY_LINK_RANGE;
    }

    public List<BlockPos> getAdjacentInventories() {
        return List.copyOf(adjacentInventories);
    }

    public int getConnectedChestCount() {
        return adjacentInventories.size();
    }

    public int getTotalSlotCount() {
        return adjacentSlotCount;
    }

    public Container getInventoryView() {
        List<Container> containers = new ArrayList<>();
        if (level != null) {
            for (BlockPos adjacentPos : adjacentInventories) {
                if (level.getBlockEntity(adjacentPos) instanceof Container container) {
                    containers.add(container);
                }
            }
        }
        return new CompositeContainer(containers);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SubColonyNodeBlockEntity blockEntity) {
        if (level.getGameTime() % SCAN_INTERVAL == 0) {
            blockEntity.scanAdjacentInventories();
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, SubColonyNodeBlockEntity blockEntity) {
            if (level.getGameTime() % 4 != 0) {
                return;
            }
            Vec3 origin = Vec3.atCenterOf(pos);
            for (BlockPos linkedPos : blockEntity.adjacentInventories) {
                if (!level.isLoaded(linkedPos)) {
                    continue;
                }
                Vec3 destination = Vec3.atCenterOf(linkedPos);
                Vec3 delta = destination.subtract(origin);
                double distance = delta.length();
                if (distance < 0.1 || distance > INVENTORY_LINK_RANGE + 1.0) {
                    continue;
                }
                Vec3 direction = delta.normalize();
                double offset = (level.getGameTime() % 40) / 40.0 * distance;
                Vec3 particlePos = origin.add(direction.scale(offset));
                int floorY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        Mth.floor(particlePos.x), Mth.floor(particlePos.z));
                level.addParticle(ModParticles.ANT_TRAIL.get(), particlePos.x, floorY + 0.08, particlePos.z,
                        direction.x * 0.012, 0.0, direction.z * 0.012);
        }
    }

    public void scanAdjacentInventories() {
        if (level == null || level.isClientSide()) {
            return;
        }
        List<BlockPos> found = adjacentInventories.stream()
                .filter(this::isWithinLinkRange)
                .filter(pos -> level.getBlockEntity(pos) instanceof Container)
                .toList();
        int slots = found.stream()
                .mapToInt(pos -> ((Container) level.getBlockEntity(pos)).getContainerSize())
                .sum();
        if (!found.equals(adjacentInventories) || slots != adjacentSlotCount) {
            adjacentInventories.clear();
            adjacentInventories.addAll(found);
            adjacentSlotCount = slots;
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private void recalculateInventorySlots() {
        adjacentSlotCount = adjacentInventories.stream()
                .filter(pos -> level != null && level.getBlockEntity(pos) instanceof Container)
                .mapToInt(pos -> ((Container) level.getBlockEntity(pos)).getContainerSize())
                .sum();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.storeNullable(QUEEN_CORE_POS_TAG, BlockPos.CODEC, queenCorePos);
        var linkedList = output.list(LINKED_INVENTORIES_TAG, BlockPos.CODEC);
        adjacentInventories.forEach(linkedList::add);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        queenCorePos = input.read(QUEEN_CORE_POS_TAG, BlockPos.CODEC).orElse(null);
        adjacentInventories.clear();
        input.listOrEmpty(LINKED_INVENTORIES_TAG, BlockPos.CODEC).forEach(adjacentInventories::add);
        recalculateInventorySlots();
    }
}
