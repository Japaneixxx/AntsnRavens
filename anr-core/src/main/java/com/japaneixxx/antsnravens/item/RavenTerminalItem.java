package com.japaneixxx.antsnravens.item;

import com.japaneixxx.antsnravens.network.ColonyNetworkLocator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public final class RavenTerminalItem extends Item {
    public RavenTerminalItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null || context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        var snapshot = ColonyNetworkLocator.scan(context.getLevel(), context.getClickedPos());
        var status = snapshot.isOperational() ? "ONLINE" : "OFFLINE";
        context.getPlayer().sendOverlayMessage(Component.translatable(
                "message.antsnravens.terminal",
                status,
                snapshot.queenChambers(),
                snapshot.subColonyNodes(),
                snapshot.radius()));
        return InteractionResult.SUCCESS;
    }
}
