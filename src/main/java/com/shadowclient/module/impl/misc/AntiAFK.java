package com.shadowclient.module.impl.misc;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import com.shadowclient.module.setting.NumberSetting;
import net.minecraft.util.Hand;

public final class AntiAFK extends Module {
    private final NumberSetting interval = add(new NumberSetting("Interval (s)", 15, 5, 60, 1));
    private int ticks;
    private boolean flip;

    public AntiAFK() {
        super("AntiAFK", "Swings and nudges your view periodically.", Category.MISC);
    }

    @Override
    public void onTick() {
        if (++ticks < interval.intValue() * 20) return;
        ticks = 0;
        flip = !flip;
        mc.player.swingHand(Hand.MAIN_HAND);
        mc.player.setYaw(mc.player.getYaw() + (flip ? 2f : -2f));
    }
}
