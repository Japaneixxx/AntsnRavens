package com.japaneixxx.antsnravens.item;

import com.japaneixxx.antsnravens.entity.CompanionCrowEntity;
import com.japaneixxx.antsnravens.init.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class TrainingWhistleItem extends Item {
    public TrainingWhistleItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        CompanionCrowEntity existing = CompanionCrowEntity.findOwned(level, player);
        if (existing != null) {
            player.sendOverlayMessage(Component.translatable("message.antsnravens.crow_already_present"));
            return InteractionResult.CONSUME;
        }
        CompanionCrowEntity crow = ModEntities.COMPANION_CROW.get().create(level, EntitySpawnReason.TRIGGERED);
        if (crow == null) {
            return InteractionResult.FAIL;
        }
        crow.setPos(player.getX() + 1.0, player.getEyeY() + 0.5, player.getZ());
        crow.setYRot(player.getYRot());
        crow.tame(player);
        level.addFreshEntity(crow);
        player.sendOverlayMessage(Component.translatable("message.antsnravens.crow_summoned"));
        return InteractionResult.CONSUME;
    }
}
