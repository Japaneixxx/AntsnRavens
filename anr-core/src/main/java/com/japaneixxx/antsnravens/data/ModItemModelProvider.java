package com.japaneixxx.antsnravens.data;

import com.japaneixxx.antsnravens.AntsNRavensCore;
import com.japaneixxx.antsnravens.init.ModItems;
import com.japaneixxx.antsnravens.init.ModBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import java.util.stream.Stream;

public final class ModItemModelProvider extends ModelProvider {
    public ModItemModelProvider(PackOutput output) {
        super(output, AntsNRavensCore.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(ModBlocks.RAVEN_TRANSLATOR.get());
        itemModels.generateFlatItem(ModItems.RAVEN_TERMINAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.PHEROMONE_BRUSH.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.TRAINING_WHISTLE.get(), ModelTemplates.FLAT_ITEM);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }
}
