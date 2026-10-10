package com.japaneixxx.antsnravens.init;

import com.japaneixxx.antsnravens.AntsNRavensCore;
import com.japaneixxx.antsnravens.item.PheromoneBrushItem;
import com.japaneixxx.antsnravens.item.RavenTerminalItem;
import com.japaneixxx.antsnravens.item.TrainingWhistleItem;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AntsNRavensCore.MOD_ID);
    public static final DeferredItem<Item> RAVEN_TERMINAL = ITEMS.register("raven_terminal",
            () -> new RavenTerminalItem(itemProperties("raven_terminal")));
    public static final DeferredItem<Item> PHEROMONE_BRUSH = ITEMS.register("pheromone_brush",
            () -> new PheromoneBrushItem(itemProperties("pheromone_brush")));
    public static final DeferredItem<Item> TRAINING_WHISTLE = ITEMS.register("training_whistle",
            () -> new TrainingWhistleItem(itemProperties("training_whistle")));

    private static Item.Properties itemProperties(String name) {
        return new Item.Properties().setId(ResourceKey.create(
                Registries.ITEM, Identifier.fromNamespaceAndPath(AntsNRavensCore.MOD_ID, name)));
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    private ModItems() {
    }
}
