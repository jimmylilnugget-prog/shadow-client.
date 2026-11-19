package com.shadowclient.module.impl.movement;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;

public final class Sprint extends Module {
    public Sprint() {
        super("Sprint", "Automatically sprints while moving forward.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        if (mc.options.forwardKey.isPressed() && !p.isSneaking() && !p.horizontalCollision
                && !p.isUsingItem() && p.getHungerManager().getFoodLevel() > 6) {
            p.setSprinting(true);
        }
    }
}
