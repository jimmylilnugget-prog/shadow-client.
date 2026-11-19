package com.shadowclient.module.impl.world;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;

public final class AutoMine extends Module {
    public AutoMine() {
        super("AutoMine", "Holds the attack key to mine continuously.", Category.WORLD);
    }

    @Override
    public void onTick() { if (mc.currentScreen == null) mc.options.attackKey.setPressed(true); }

    @Override
    protected void onDisable() { mc.options.attackKey.setPressed(false); }
}
