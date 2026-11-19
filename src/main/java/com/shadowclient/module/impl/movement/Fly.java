package com.shadowclient.module.impl.movement;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import com.shadowclient.module.setting.NumberSetting;

public final class Fly extends Module {
    private final NumberSetting speed = add(new NumberSetting("Speed", 1.5, 0.5, 5.0, 0.1));

    public Fly() {
        super("Fly", "Creative-style flight.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        mc.player.getAbilities().allowFlying = true;
        mc.player.getAbilities().flying = true;
        mc.player.getAbilities().setFlySpeed(speed.floatValue() * 0.05f);
    }

    @Override
    protected void onDisable() {
        if (mc.player == null) return;
        mc.player.getAbilities().setFlySpeed(0.05f);
        if (!mc.player.isCreative() && !mc.player.isSpectator()) {
            mc.player.getAbilities().flying = false;
            mc.player.getAbilities().allowFlying = false;
        }
    }
}
