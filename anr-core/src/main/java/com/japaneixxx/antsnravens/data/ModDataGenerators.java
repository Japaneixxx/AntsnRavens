package com.japaneixxx.antsnravens.data;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class ModDataGenerators {
    private ModDataGenerators() {
    }

    public static void gatherData(GatherDataEvent.Client event) {
        event.addProvider(new ModItemModelProvider(event.getGenerator().getPackOutput()));
    }
}
