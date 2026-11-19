package com.shadowclient.module.impl.combat;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import com.shadowclient.module.setting.BooleanSetting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

public final class TriggerBot extends Module {
    private final BooleanSetting players = add(new BooleanSetting("Players", true));
    private final BooleanSetting others = add(new BooleanSetting("Other Mobs", true));

    public TriggerBot() {
        super("TriggerBot", "Attacks whatever your crosshair is on once the cooldown is ready.", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.currentScreen != null) return;
        if (!(mc.crosshairTarget instanceof EntityHitResult hit)) return;
        if (!(hit.getEntity() instanceof LivingEntity le) || !le.isAlive()) return;
        boolean isPlayer = le instanceof PlayerEntity;
        if (isPlayer ? !players.get() : !others.get()) return;
        if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
            mc.interactionManager.attackEntity(mc.player, le);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }
}
