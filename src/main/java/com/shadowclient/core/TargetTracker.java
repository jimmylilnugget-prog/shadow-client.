package com.shadowclient.core;

import com.shadowclient.ShadowClient;
import com.shadowclient.module.impl.combat.KillAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;

/** Remembers the most relevant target (KillAura target or crosshair entity) for the HUD. */
public final class TargetTracker {
    private static final long LINGER_MS = 3000;
    private static LivingEntity target;
    private static long lastSeen;

    private TargetTracker() {}

    public static void update() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) { target = null; return; }

        KillAura aura = ShadowClient.modules().get(KillAura.class);
        LivingEntity t = aura != null ? aura.getTarget() : null;
        if (t == null && mc.crosshairTarget instanceof EntityHitResult hit
                && hit.getEntity() instanceof LivingEntity le && le.isAlive()) {
            t = le;
        }

        long now = System.currentTimeMillis();
        if (t != null) {
            target = t;
            lastSeen = now;
        } else if (target != null && (!target.isAlive() || now - lastSeen > LINGER_MS)) {
            target = null;
        }
    }

    public static LivingEntity get() { return target; }
}
