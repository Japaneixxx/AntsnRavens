package com.japaneixxx.antsnravens.block;

import com.japaneixxx.antsnravens.block.entity.SubColonyNodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.japaneixxx.antsnravens.init.ModBlockEntities;

public final class SubColonyNodeBlock extends BaseEntityBlock {
    public SubColonyNodeBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SubColonyNodeBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntities.SUB_COLONY_NODE_BE.get(),
                level.isClientSide() ? SubColonyNodeBlockEntity::clientTick : SubColonyNodeBlockEntity::serverTick);
    }
}
