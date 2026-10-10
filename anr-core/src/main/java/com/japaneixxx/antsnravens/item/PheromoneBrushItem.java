package com.japaneixxx.antsnravens.item;

import com.japaneixxx.antsnravens.block.entity.QueenChamberBlockEntity;
import com.japaneixxx.antsnravens.block.entity.SubColonyNodeBlockEntity;
import com.japaneixxx.antsnravens.init.ModBlocks;
import com.japaneixxx.antsnravens.init.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.server.level.ServerLevel;

public final class PheromoneBrushItem extends Item {
    public PheromoneBrushItem(Properties properties) {
        super(properties.stacksTo(1).durability(128));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var player = context.getPlayer();
        if (player == null || level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        var stack = context.getItemInHand();
        BlockPos clickedPos = context.getClickedPos();
        var state = level.getBlockState(clickedPos);

        if (state.is(ModBlocks.SUB_COLONY_NODE.get())) {
            stack.set(ModDataComponents.SELECTED_NODE_POS.get(), clickedPos.immutable());
            stack.remove(ModDataComponents.SELECTED_INVENTORY_POS.get());
            player.sendOverlayMessage(Component.translatable("message.antsnravens.node_selected"));
            return InteractionResult.SUCCESS;
        }

        BlockPos selectedNodePos = stack.get(ModDataComponents.SELECTED_NODE_POS.get());
        if (selectedNodePos != null && level.getBlockEntity(clickedPos) instanceof Container
                && level.getBlockEntity(selectedNodePos) instanceof SubColonyNodeBlockEntity nodeBE) {
            if (!nodeBE.linkInventory(clickedPos)) {
                player.sendOverlayMessage(Component.translatable("message.antsnravens.inventory_out_of_range"));
                return InteractionResult.SUCCESS;
            }
            stack.remove(ModDataComponents.SELECTED_NODE_POS.get());
            level.playSound(null, clickedPos, SoundEvents.BEE_POLLINATE, SoundSource.BLOCKS, 1.0f, 1.2f);
            player.sendOverlayMessage(Component.translatable("message.antsnravens.inventory_linked"));
            stack.hurtAndBreak(1, player, context.getHand());
            return InteractionResult.SUCCESS;
        }

        if (!state.is(ModBlocks.QUEEN_CHAMBER.get())) {
            return InteractionResult.PASS;
        }

        if (selectedNodePos == null) {
            player.sendOverlayMessage(Component.translatable("message.antsnravens.select_node_first"));
            return InteractionResult.SUCCESS;
        }

        var queenEntity = level.getBlockEntity(clickedPos);
        var nodeEntity = level.getBlockEntity(selectedNodePos);
        if (!(queenEntity instanceof QueenChamberBlockEntity queenBE)
                || !(nodeEntity instanceof SubColonyNodeBlockEntity nodeBE)) {
            stack.remove(ModDataComponents.SELECTED_NODE_POS.get());
            player.sendOverlayMessage(Component.translatable("message.antsnravens.invalid_pair"));
            return InteractionResult.SUCCESS;
        }

        queenBE.registerSubColony(selectedNodePos);
        nodeBE.setQueenCore(clickedPos);
        stack.remove(ModDataComponents.SELECTED_NODE_POS.get());
        level.playSound(null, clickedPos, SoundEvents.BEE_POLLINATE, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    clickedPos.getX() + 0.5, clickedPos.getY() + 1.0, clickedPos.getZ() + 0.5,
                    12, 0.4, 0.5, 0.4, 0.05);
        }
        player.sendOverlayMessage(Component.translatable("message.antsnravens.node_linked"));
        stack.hurtAndBreak(1, player, context.getHand());
        return InteractionResult.SUCCESS;
    }
}
