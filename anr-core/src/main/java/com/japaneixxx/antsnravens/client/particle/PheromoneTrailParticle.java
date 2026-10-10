package com.japaneixxx.antsnravens.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public final class PheromoneTrailParticle extends SimpleAnimatedParticle {
    private PheromoneTrailParticle(ClientLevel level, double x, double y, double z,
            double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, sprites, 0.35f);
        setParticleSpeed(xd, yd, zd);
        setColor(0xA8D83C);
        setAlpha(0.72f);
        scale(0.55f);
        setLifetime(18 + random.nextInt(10));
    }

    public static PheromoneTrailParticle create(SimpleParticleType type, ClientLevel level,
            double x, double y, double z, double xd, double yd, double zd, RandomSource random,
            SpriteSet sprites) {
        return new PheromoneTrailParticle(level, x, y, z, xd, yd, zd, sprites);
    }
}
