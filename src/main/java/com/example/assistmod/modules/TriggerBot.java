package com.example.assistmod.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

/** Бьёт цель, на которую наведён прицел, когда оружие перезарядилось. */
public class TriggerBot extends Module {
    // Настройки
    public boolean attackPlayers = true;
    public boolean attackMobs = true;
    public float minCooldown = 1.0f; // 0..1, 1 = полная перезарядка (макс. урон)

    public TriggerBot() {
        super("TriggerBot");
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null || mc.interactionManager == null || mc.currentScreen != null) return;
        if (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.ENTITY) return;

        Entity target = ((EntityHitResult) mc.crosshairTarget).getEntity();
        if (!isValid(mc, target)) return;
        if (mc.player.getAttackCooldownProgress(0.0f) < minCooldown) return;

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private boolean isValid(MinecraftClient mc, Entity e) {
        if (!(e instanceof LivingEntity le) || e == mc.player) return false;
        if (!le.isAlive() || le.isSpectator()) return false;
        return e instanceof PlayerEntity ? attackPlayers : attackMobs;
    }
}
