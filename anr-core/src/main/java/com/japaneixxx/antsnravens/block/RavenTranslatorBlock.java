package com.japaneixxx.antsnravens.block;

import com.japaneixxx.antsnravens.block.entity.QueenChamberBlockEntity;
import com.japaneixxx.antsnravens.block.entity.RavenTranslatorBlockEntity;
import com.japaneixxx.antsnravens.init.ModBlockEntities;
import com.japaneixxx.antsnravens.world.inventory.RavenTerminalMenu;
import com.japaneixxx.antsnravens.entity.CompanionCrowEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.extensions.IPlayerExtension;
import org.jetbrains.annotations.Nullable;

public final class RavenTranslatorBlock extends BaseEntityBlock {
    public RavenTranslatorBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RavenTranslatorBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hit) {
        if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity translator = level.getBlockEntity(pos);
        if (!(translator instanceof RavenTranslatorBlockEntity translatorEntity)) {
            return InteractionResult.CONSUME;
        }

        BlockPos queenPos = translatorEntity.getQueenCorePos();
        if (queenPos == null) {
            queenPos = findNearbyQueen(level, pos);
        }
        BlockEntity queen = queenPos == null ? null : level.getBlockEntity(queenPos);
        if (queen instanceof QueenChamberBlockEntity queenEntity
                && translatorEntity.getQueenCorePos() == null) {
            translatorEntity.setQueenCorePos(queenEntity.getBlockPos());
            queenPos = queenEntity.getBlockPos();
        }
        if (!(queen instanceof QueenChamberBlockEntity queenEntity)) {
            serverPlayer.sendOverlayMessage(Component.translatable("message.antsnravens.translator_unlinked"));
            return InteractionResult.CONSUME;
        }

        CompanionCrowEntity crow = CompanionCrowEntity.findNearby(level, serverPlayer, pos);
        if (crow != null) {
            crow.beginTranslatorVisit(serverPlayer, pos, queenEntity.getBlockPos());
            return InteractionResult.CONSUME;
        }

        SimpleMenuProvider menuProvider = new SimpleMenuProvider(
                (containerId, inventory, ignored) -> new RavenTerminalMenu(
                        containerId, inventory, queenEntity),
                Component.translatable("container.antsnravens.raven_terminal"));
        ((IPlayerExtension) serverPlayer).openMenu(menuProvider,
                buffer -> buffer.writeBlockPos(queenEntity.getBlockPos()));
        return InteractionResult.CONSUME;
    }

    private static @Nullable BlockPos findNearbyQueen(Level level, BlockPos translatorPos) {
        BlockPos min = translatorPos.offset(-32, -16, -32);
        BlockPos max = translatorPos.offset(32, 16, 32);
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (BlockPos candidate : BlockPos.betweenClosed(min, max)) {
            if (!(level.getBlockEntity(candidate) instanceof QueenChamberBlockEntity)) {
                continue;
            }
            double distance = candidate.distSqr(translatorPos);
            if (distance < nearestDistance) {
                nearest = candidate.immutable();
                nearestDistance = distance;
            }
        }
        return nearest;
    }
}
