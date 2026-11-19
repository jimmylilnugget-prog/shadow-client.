package com.shadowclient.module.impl.world;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import com.shadowclient.module.setting.NumberSetting;

public final class AutoUse extends Module {
    private final NumberSetting delay = add(new NumberSetting("Delay (ticks)", 4, 0, 20, 1));
    private int counter;

    public AutoUse() {
        super("AutoUse", "Repeatedly presses the use key (place blocks, eat, etc.).", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (mc.currentScreen != null) return;
        mc.options.useKey.setPressed(counter++ % (delay.intValue() + 1) == 0);
    }

    @Override
    protected void onDisable() { mc.options.useKey.setPressed(false); }
}
