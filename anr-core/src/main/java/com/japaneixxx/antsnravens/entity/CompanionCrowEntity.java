package com.japaneixxx.antsnravens.entity;

import com.japaneixxx.antsnravens.block.entity.QueenChamberBlockEntity;
import com.japaneixxx.antsnravens.init.ModEntities;
import com.japaneixxx.antsnravens.world.inventory.RavenTerminalMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IPlayerExtension;

import java.util.List;

public final class CompanionCrowEntity extends Parrot {
    private static final int FLIGHT_TICKS = 24;
    private Player terminalPlayer;
    private BlockPos translatorPos;
    private BlockPos queenPos;
    private Vec3 flightStart;
    private Vec3 flightEnd;
    private int flightTick;
    private boolean returningToOwner;

    public CompanionCrowEntity(EntityType<? extends Parrot> entityType, Level level) {
        super(entityType, level);
        setTame(true, false);
    }

    @Override
    protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) {
        return new FlyingPathNavigation(this, level);
    }

    public static CompanionCrowEntity findNearby(Level level, Player player, BlockPos translatorPos) {
        AABB search = new AABB(translatorPos).inflate(15.0);
        List<CompanionCrowEntity> crows = level.getEntitiesOfClass(CompanionCrowEntity.class, search,
                crow -> crow.isOwnedBy(player));
        return crows.stream().findFirst().orElse(null);
    }

    public static CompanionCrowEntity findOwned(Level level, Player player) {
        return level.getEntitiesOfClass(CompanionCrowEntity.class,
                player.getBoundingBox().inflate(32.0), crow -> crow.isOwnedBy(player))
                .stream().findFirst().orElse(null);
    }

    public void beginTranslatorVisit(ServerPlayer player, BlockPos translatorPos, BlockPos queenPos) {
        if (level().isClientSide() || flightTick > 0) {
            return;
        }
        this.terminalPlayer = player;
        this.translatorPos = translatorPos.immutable();
        this.queenPos = queenPos.immutable();
        this.flightStart = position();
        this.flightEnd = Vec3.atCenterOf(translatorPos).add(0.0, 1.2, 0.0);
        this.flightTick = 1;
        this.returningToOwner = false;
        setNoGravity(true);
        setOrderedToSit(false);
    }

    public void returnToOwner() {
        if (level().isClientSide() || getOwnerReference() == null || flightTick > 0) {
            return;
        }
        LivingEntity owner = getOwnerReference().getEntity(level(), LivingEntity.class);
        if (owner == null) {
            return;
        }
        flightStart = position();
        flightEnd = owner.position().add(0.0, owner.getBbHeight() + 0.25, 0.0);
        flightTick = 1;
        returningToOwner = true;
        setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide() || flightTick <= 0) {
            return;
        }

        float t = Math.min(1.0f, flightTick / (float) FLIGHT_TICKS);
        Vec3 control = flightStart.lerp(flightEnd, 0.5).add(0.0, 2.0, 0.0);
        Vec3 point = bezier(flightStart, control, flightEnd, t);
        setPos(point.x, point.y, point.z);
        flightTick++;
        if (t < 1.0f) {
            return;
        }
        flightTick = 0;
        setNoGravity(false);
        if (returningToOwner) {
            if (getOwnerReference() != null) {
                LivingEntity owner = getOwnerReference().getEntity(level(), LivingEntity.class);
                if (owner instanceof ServerPlayer serverPlayer && setEntityOnShoulder(serverPlayer)) {
                    discard();
                    return;
                }
            }
            returningToOwner = false;
            return;
        }
        setDeltaMovement(Vec3.ZERO);
        level().playSound(null, blockPosition(), SoundEvents.PARROT_IMITATE_CREEPER,
                SoundSource.NEUTRAL, 0.8f, 1.2f);
        if (terminalPlayer instanceof ServerPlayer player && queenPos != null) {
            openTerminal(player);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, net.minecraft.world.InteractionHand hand) {
        InteractionResult result = interact(this, player);
        return result.consumesAction() ? result : super.mobInteract(player, hand);
    }

    private static Vec3 bezier(Vec3 start, Vec3 control, Vec3 end, float t) {
        double inverse = 1.0 - t;
        return start.scale(inverse * inverse)
                .add(control.scale(2.0 * inverse * t))
                .add(end.scale(t * t));
    }

    private void openTerminal(ServerPlayer player) {
        BlockPos targetQueen = queenPos;
        SimpleMenuProvider provider = new SimpleMenuProvider(
                (containerId, inventory, ignored) -> new RavenTerminalMenu(
                        containerId, inventory, targetQueen, this),
                Component.translatable("container.antsnravens.raven_terminal"));
        ((IPlayerExtension) player).openMenu(provider, buffer -> buffer.writeBlockPos(targetQueen));
        terminalPlayer = null;
    }

    public static InteractionResult interact(CompanionCrowEntity crow, Player player) {
        if (player instanceof ServerPlayer serverPlayer
                && crow.isOwnedBy(player)
                && crow.translatorPos != null && crow.queenPos != null) {
            crow.openTerminal(serverPlayer);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
