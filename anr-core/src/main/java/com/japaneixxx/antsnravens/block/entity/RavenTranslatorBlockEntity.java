package com.japaneixxx.antsnravens.block.entity;

import com.japaneixxx.antsnravens.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class RavenTranslatorBlockEntity extends BlockEntity {
    private static final String QUEEN_CORE_POS_TAG = "queen_core_pos";
    private BlockPos queenCorePos;

    public RavenTranslatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RAVEN_TRANSLATOR_BE.get(), pos, state);
    }

    public BlockPos getQueenCorePos() {
        return queenCorePos;
    }

    public void setQueenCorePos(BlockPos queenCorePos) {
        this.queenCorePos = queenCorePos == null ? null : queenCorePos.immutable();
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.storeNullable(QUEEN_CORE_POS_TAG, BlockPos.CODEC, queenCorePos);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        queenCorePos = input.read(QUEEN_CORE_POS_TAG, BlockPos.CODEC).orElse(null);
    }
}
