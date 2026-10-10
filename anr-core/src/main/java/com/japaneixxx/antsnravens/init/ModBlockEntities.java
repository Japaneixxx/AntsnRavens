package com.japaneixxx.antsnravens.init;

import com.japaneixxx.antsnravens.AntsNRavensCore;
import com.japaneixxx.antsnravens.block.entity.QueenChamberBlockEntity;
import com.japaneixxx.antsnravens.block.entity.SubColonyNodeBlockEntity;
import com.japaneixxx.antsnravens.block.entity.RavenTranslatorBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AntsNRavensCore.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QueenChamberBlockEntity>> QUEEN_CHAMBER_BE =
            BLOCK_ENTITIES.register("queen_chamber_be",
                    () -> new BlockEntityType<>(QueenChamberBlockEntity::new, ModBlocks.QUEEN_CHAMBER.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SubColonyNodeBlockEntity>> SUB_COLONY_NODE_BE =
            BLOCK_ENTITIES.register("sub_colony_node_be",
                    () -> new BlockEntityType<>(SubColonyNodeBlockEntity::new, ModBlocks.SUB_COLONY_NODE.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RavenTranslatorBlockEntity>> RAVEN_TRANSLATOR_BE =
            BLOCK_ENTITIES.register("raven_translator_be",
                    () -> new BlockEntityType<>(RavenTranslatorBlockEntity::new, ModBlocks.RAVEN_TRANSLATOR.get()));

    private ModBlockEntities() {
    }
}
