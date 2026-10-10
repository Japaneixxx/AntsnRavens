package com.japaneixxx.antsnravens.init;

import com.japaneixxx.antsnravens.AntsNRavensCore;
import com.japaneixxx.antsnravens.entity.CompanionCrowEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.bus.api.IEventBus;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, AntsNRavensCore.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<CompanionCrowEntity>> COMPANION_CROW =
            ENTITIES.register("companion_crow", () -> EntityType.Builder
                    .of(CompanionCrowEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.9f)
                    .clientTrackingRange(8)
                    .updateInterval(2)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath(AntsNRavensCore.MOD_ID, "companion_crow"))));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }

    private ModEntities() {
    }
}
