package com.japaneixxx.antsnravens;

import com.japaneixxx.antsnravens.init.ModBlockEntities;
import com.japaneixxx.antsnravens.init.ModBlocks;
import com.japaneixxx.antsnravens.init.ModItems;
import com.japaneixxx.antsnravens.init.ModDataComponents;
import com.japaneixxx.antsnravens.init.ModMenuTypes;
import com.japaneixxx.antsnravens.init.ModEntities;
import com.japaneixxx.antsnravens.init.ModParticles;
import com.japaneixxx.antsnravens.init.ModEntityAttributes;
import com.japaneixxx.antsnravens.init.ModCapabilities;
import com.japaneixxx.antsnravens.data.ModDataGenerators;
import com.japaneixxx.antsnravens.client.ModClientEvents;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(AntsNRavensCore.MOD_ID)
public final class AntsNRavensCore {
    public static final String MOD_ID = "antsnravens";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
            CREATIVE_TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.antsnravens"))
                    .icon(() -> ModItems.RAVEN_TERMINAL.get().getDefaultInstance())
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.RAVEN_TERMINAL.get());
                        output.accept(ModItems.PHEROMONE_BRUSH.get());
                        output.accept(ModItems.TRAINING_WHISTLE.get());
                        output.accept(ModBlocks.QUEEN_CHAMBER_ITEM.get());
                        output.accept(ModBlocks.SUB_COLONY_NODE_ITEM.get());
                        output.accept(ModBlocks.RAVEN_TRANSLATOR_ITEM.get());
                    }).build());

    public AntsNRavensCore(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(ModDataGenerators::gatherData);
        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModDataComponents.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModEntities.register(modBus);
        ModParticles.register(modBus);
        modBus.addListener(ModEntityAttributes::register);
        modBus.addListener(ModCapabilities::register);
        ModMenuTypes.MENUS.register(modBus);
        modBus.addListener(ModClientEvents::registerScreens);
        modBus.addListener(ModClientEvents::registerRenderers);
        modBus.addListener(ModClientEvents::registerParticles);
        CREATIVE_TABS.register(modBus);
        modContainer.registerConfig(ModConfig.Type.SYNCED, ModConfigSpecProvider.SPEC);
        LOGGER.info("Ants 'n Ravens core initialized");
    }
}
