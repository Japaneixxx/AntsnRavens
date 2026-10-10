package com.japaneixxx.antsnravens.block.entity;

import com.japaneixxx.antsnravens.init.ModBlockEntities;
import com.japaneixxx.antsnravens.block.SubColonyNodeBlock;
import com.japaneixxx.antsnravens.init.ModParticles;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class QueenChamberBlockEntity extends BlockEntity {
    private static final String CONNECTED_SUB_COLONIES_TAG = "connected_sub_colonies";
    private final List<BlockPos> connectedSubColonies = new ArrayList<>();
    private final SimpleContainer centralInventory = new SimpleContainer(27);

    public QueenChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.QUEEN_CHAMBER_BE.get(), pos, state);
    }

    public List<BlockPos> getConnectedSubColonies() {
        return List.copyOf(connectedSubColonies);
    }

    public Container getCentralInventory() {
        return centralInventory;
    }

    public void connectSubColony(BlockPos subColonyPos) {
        if (!connectedSubColonies.contains(subColonyPos)) {
            connectedSubColonies.add(subColonyPos.immutable());
            setChanged();
            notifyClients();
        }

    }

    public void registerSubColony(BlockPos subColonyPos) {
        connectSubColony(subColonyPos);
    }

    public void disconnectSubColony(BlockPos subColonyPos) {
        if (connectedSubColonies.remove(subColonyPos)) {
            setChanged();
            notifyClients();
        }
    }

    private void notifyClients() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, QueenChamberBlockEntity blockEntity) {
        if (level.isClientSide()) {
            clientTick(level, pos, state, blockEntity);
            return;
        }
        if (level.getGameTime() % 20 != 0) {
            return;
        }
        blockEntity.connectedSubColonies.removeIf(subColonyPos ->
                !level.isLoaded(subColonyPos)
                        || !(level.getBlockState(subColonyPos).getBlock() instanceof SubColonyNodeBlock));
        blockEntity.setChanged();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, QueenChamberBlockEntity blockEntity) {
        if (level.getGameTime() % 4 != 0) {
            return;
        }
        Vec3 origin = Vec3.atCenterOf(pos).add(0.0, 0.65, 0.0);
        for (BlockPos target : blockEntity.connectedSubColonies) {
            if (!level.isLoaded(target)) {
                continue;
            }
            Vec3 destination = Vec3.atCenterOf(target).add(0.0, 0.65, 0.0);
            Vec3 delta = destination.subtract(origin);
            double distance = delta.length();
            if (distance > 96.0 || distance < 0.1) {
                continue;
            }
            Vec3 direction = delta.normalize();
            double offset = (level.getGameTime() % 32) / 32.0 * distance;
            Vec3 particlePos = origin.add(direction.scale(offset));
            level.addParticle(ModParticles.PHEROMONE_TRAIL.get(), particlePos.x, particlePos.y, particlePos.z,
                    direction.x * 0.01, direction.y * 0.01, direction.z * 0.01);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        var outputList = output.list(CONNECTED_SUB_COLONIES_TAG, BlockPos.CODEC);
        connectedSubColonies.forEach(outputList::add);
        centralInventory.storeAsItemList(output.list("central_inventory", ItemStack.CODEC));
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        connectedSubColonies.clear();
        input.listOrEmpty(CONNECTED_SUB_COLONIES_TAG, BlockPos.CODEC).forEach(connectedSubColonies::add);
        centralInventory.fromItemList(input.listOrEmpty("central_inventory", ItemStack.CODEC));
    }
}
