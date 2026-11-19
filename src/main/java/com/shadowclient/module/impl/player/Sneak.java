package com.shadowclient.module.impl.player;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;

public final class Sneak extends Module {
    public Sneak() {
        super("Sneak", "Holds the sneak key for you.", Category.PLAYER);
    }

    @Override
    public void onTick() { mc.options.sneakKey.setPressed(true); }

    @Override
    protected void onDisable() { mc.options.sneakKey.setPressed(false); }
}
