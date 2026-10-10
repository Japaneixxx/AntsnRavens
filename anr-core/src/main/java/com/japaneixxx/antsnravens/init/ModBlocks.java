package com.japaneixxx.antsnravens.init;

import com.japaneixxx.antsnravens.AntsNRavensCore;
import com.japaneixxx.antsnravens.block.QueenChamberBlock;
import com.japaneixxx.antsnravens.block.SubColonyNodeBlock;
import com.japaneixxx.antsnravens.block.RavenTranslatorBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AntsNRavensCore.MOD_ID);
    public static final DeferredBlock<Block> QUEEN_CHAMBER = BLOCKS.register("queen_chamber",
            () -> new QueenChamberBlock(blockProperties("queen_chamber").mapColor(MapColor.COLOR_PURPLE).strength(4.0f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SUB_COLONY_NODE = BLOCKS.register("sub_colony_node",
            () -> new SubColonyNodeBlock(blockProperties("sub_colony_node").mapColor(MapColor.COLOR_BROWN).strength(2.5f).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> RAVEN_TRANSLATOR = BLOCKS.register("raven_translator",
            () -> new RavenTranslatorBlock(blockProperties("raven_translator").mapColor(MapColor.COLOR_BLUE).strength(2.0f)));
    public static final DeferredItem<BlockItem> QUEEN_CHAMBER_ITEM = registerItem("queen_chamber", QUEEN_CHAMBER);
    public static final DeferredItem<BlockItem> SUB_COLONY_NODE_ITEM = registerItem("sub_colony_node", SUB_COLONY_NODE);
    public static final DeferredItem<BlockItem> RAVEN_TRANSLATOR_ITEM = registerItem("raven_translator", RAVEN_TRANSLATOR);

    private static DeferredItem<BlockItem> registerItem(String name, DeferredBlock<Block> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), itemProperties(name)));
    }

    private static BlockBehaviour.Properties blockProperties(String name) {
        return BlockBehaviour.Properties.of().setId(ResourceKey.create(
                Registries.BLOCK, Identifier.fromNamespaceAndPath(AntsNRavensCore.MOD_ID, name)));
    }

    private static Item.Properties itemProperties(String name) {
        return new Item.Properties().setId(ResourceKey.create(
                Registries.ITEM, Identifier.fromNamespaceAndPath(AntsNRavensCore.MOD_ID, name)));
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }

    private ModBlocks() {
    }
}
