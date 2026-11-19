package com.shadowclient.module.impl.movement;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import com.shadowclient.module.setting.NumberSetting;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;

public final class Step extends Module {
    private final NumberSetting height = add(new NumberSetting("Height", 1.5, 0.6, 3.0, 0.1));

    public Step() {
        super("Step", "Step up blocks without jumping.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        EntityAttributeInstance attr = mc.player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (attr != null) attr.setBaseValue(height.get());
    }

    @Override
    protected void onDisable() {
        if (mc.player == null) return;
        EntityAttributeInstance attr = mc.player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (attr != null) attr.setBaseValue(0.6);
    }
}
