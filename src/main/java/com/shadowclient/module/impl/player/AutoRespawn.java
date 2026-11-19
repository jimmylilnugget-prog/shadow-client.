package com.shadowclient.module.impl.player;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;

public final class AutoRespawn extends Module {
    public AutoRespawn() {
        super("AutoRespawn", "Respawns instantly when you die.", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.player.isDead()) mc.player.requestRespawn();
    }
}
