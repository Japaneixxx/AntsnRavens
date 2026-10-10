package com.japaneixxx.antsnravens;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ModConfigSpecProvider {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.IntValue NETWORK_SCAN_RADIUS = BUILDER
            .comment("Maximum distance used when discovering local colony nodes.")
            .defineInRange("networkScanRadius", 32, 4, 128);
    public static final ModConfigSpec SPEC = BUILDER.build();

    private ModConfigSpecProvider() {
    }
}
