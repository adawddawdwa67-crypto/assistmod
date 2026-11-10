package com.example.assistmod.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Плавно подтягивает прицел к ближайшей цели в поле зрения. */
public class AimAssist extends Module {
    // Настройки
    public double range = 4.5;       // дальность, блоков
    public float fov = 60f;          // угол захвата цели, градусов
    public float smoothing = 0.15f;  // доля оставшегося угла за тик (меньше = плавнее)
    public float maxStep = 6f;       // макс. поворот за тик, градусов
    public boolean players = true;
    public boolean mobs = true;
    public boolean requireLineOfSight = true;

    public AimAssist() {
        super("AimAssist");
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null || mc.currentScreen != null) return;

        LivingEntity target = findTarget(mc);
        if (target == null) return;

        Vec3d eyes = mc.player.getEyePos();
        Vec3d aim = target.getBoundingBox().getCenter();
        double dx = aim.x - eyes.x;
        double dy = aim.y - eyes.y;
        double dz = aim.z - eyes.z;

        float wantYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float wantPitch = (float) -Math.toDegrees(Math.atan2(dy, Math.hypot(dx, dz)));

        float dYaw = MathHelper.wrapDegrees(wantYaw - mc.player.getYaw());
        float dPitch = wantPitch - mc.player.getPitch();

        mc.player.setYaw(mc.player.getYaw() + MathHelper.clamp(dYaw * smoothing, -maxStep, maxStep));
        mc.player.setPitch(MathHelper.clamp(
                mc.player.getPitch() + MathHelper.clamp(dPitch * smoothing, -maxStep, maxStep), -90f, 90f));
    }

    private LivingEntity findTarget(MinecraftClient mc) {
        LivingEntity best = null;
        double bestAngle = fov;

        for (var e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity le) || e == mc.player) continue;
            if (!le.isAlive() || le.isSpectator()) continue;
            if (e instanceof PlayerEntity ? !players : !mobs) continue;
            if (mc.player.distanceTo(e) > range) continue;
            if (requireLineOfSight && !mc.player.canSee(e)) continue;

            Vec3d eyes = mc.player.getEyePos();
            Vec3d aim = le.getBoundingBox().getCenter();
            double dx = aim.x - eyes.x, dy = aim.y - eyes.y, dz = aim.z - eyes.z;
            float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
            float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.hypot(dx, dz)));
            double angle = Math.hypot(
                    MathHelper.wrapDegrees(yaw - mc.player.getYaw()),
                    pitch - mc.player.getPitch());

            if (angle < bestAngle) {
                bestAngle = angle;
                best = le;
            }
        }
        return best;
    }
}
