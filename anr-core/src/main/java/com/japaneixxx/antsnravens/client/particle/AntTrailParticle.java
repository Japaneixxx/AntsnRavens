package com.japaneixxx.antsnravens.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public final class AntTrailParticle extends SimpleAnimatedParticle {
    private AntTrailParticle(ClientLevel level, double x, double y, double z,
            double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z, sprites, 0.0f);
        setParticleSpeed(xd, yd, zd);
        setColor(0.015f, 0.01f, 0.008f);
        setAlpha(0.95f);
        scale(0.18f);
        setLifetime(12 + random.nextInt(8));
    }

    public static AntTrailParticle create(SimpleParticleType type, ClientLevel level,
            double x, double y, double z, double xd, double yd, double zd, RandomSource random,
            SpriteSet sprites) {
        return new AntTrailParticle(level, x, y, z, xd, yd, zd, sprites);
    }
}
