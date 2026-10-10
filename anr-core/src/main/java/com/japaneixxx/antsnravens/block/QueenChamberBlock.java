package com.japaneixxx.antsnravens.block;

import com.japaneixxx.antsnravens.block.entity.QueenChamberBlockEntity;
import com.japaneixxx.antsnravens.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class QueenChamberBlock extends BaseEntityBlock {
    public QueenChamberBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new QueenChamberBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide()
                ? createTickerHelper(blockEntityType, ModBlockEntities.QUEEN_CHAMBER_BE.get(),
                QueenChamberBlockEntity::clientTick)
                : createTickerHelper(blockEntityType, ModBlockEntities.QUEEN_CHAMBER_BE.get(),
                QueenChamberBlockEntity::serverTick);
    }
}
