package com.japaneixxx.antsnravens.init;

import com.japaneixxx.antsnravens.AntsNRavensCore;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.bus.api.IEventBus;

public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, AntsNRavensCore.MOD_ID);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> PHEROMONE_TRAIL =
            PARTICLES.register("pheromone_trail", () -> new SimpleParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ANT_TRAIL =
            PARTICLES.register("ant_trail", () -> new SimpleParticleType(false));

    public static void register(IEventBus bus) {
        PARTICLES.register(bus);
    }

    private ModParticles() {
    }
}
