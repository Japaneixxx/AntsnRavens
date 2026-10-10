package com.japaneixxx.antsnravens.init;

import com.japaneixxx.antsnravens.block.entity.QueenChamberBlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;

public final class ModCapabilities {
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlockEntities.QUEEN_CHAMBER_BE.get(),
                (blockEntity, side) -> centralInventory(blockEntity, side));
    }

    private static ResourceHandler<ItemResource> centralInventory(QueenChamberBlockEntity blockEntity,
            net.minecraft.core.Direction side) {
        return VanillaContainerWrapper.of(blockEntity.getCentralInventory());
    }

    private ModCapabilities() {
    }
}
