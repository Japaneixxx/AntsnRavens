package com.japaneixxx.antsnravens.init;

import com.japaneixxx.antsnravens.entity.CompanionCrowEntity;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public final class ModEntityAttributes {
    public static void register(EntityAttributeCreationEvent event) {
        event.put(ModEntities.COMPANION_CROW.get(), CompanionCrowEntity.createAttributes().build());
    }

    private ModEntityAttributes() {
    }
}
