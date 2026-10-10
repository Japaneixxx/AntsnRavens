package com.japaneixxx.antsnravens.client;

import com.japaneixxx.antsnravens.client.gui.RavenTerminalScreen;
import com.japaneixxx.antsnravens.init.ModMenuTypes;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import com.japaneixxx.antsnravens.client.entity.CompanionCrowRenderer;
import com.japaneixxx.antsnravens.client.particle.PheromoneTrailParticle;
import com.japaneixxx.antsnravens.client.particle.AntTrailParticle;
import com.japaneixxx.antsnravens.init.ModEntities;
import com.japaneixxx.antsnravens.init.ModParticles;

public final class ModClientEvents {
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.RAVEN_TERMINAL.get(), RavenTerminalScreen::new);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.COMPANION_CROW.get(), CompanionCrowRenderer::new);
    }

    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.PHEROMONE_TRAIL.get(), (sprites) ->
                (type, level, x, y, z, xd, yd, zd, random) ->
                        PheromoneTrailParticle.create(type, level, x, y, z, xd, yd, zd, random, sprites));
        event.registerSpriteSet(ModParticles.ANT_TRAIL.get(), (sprites) ->
                (type, level, x, y, z, xd, yd, zd, random) ->
                        AntTrailParticle.create(type, level, x, y, z, xd, yd, zd, random, sprites));
    }

    private ModClientEvents() {
    }
}
