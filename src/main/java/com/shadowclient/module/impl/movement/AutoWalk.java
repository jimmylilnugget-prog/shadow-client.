package com.shadowclient.module.impl.movement;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;

public final class AutoWalk extends Module {
    public AutoWalk() {
        super("AutoWalk", "Holds the forward key for you.", Category.MOVEMENT);
    }

    @Override
    public void onTick() { mc.options.forwardKey.setPressed(true); }

    @Override
    protected void onDisable() { mc.options.forwardKey.setPressed(false); }
}
