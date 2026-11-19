package com.shadowclient.module.impl.render;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public final class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", "Maximum brightness everywhere (client-side night vision).", Category.RENDER);
    }

    @Override
    public void onTick() {
        StatusEffectInstance cur = mc.player.getStatusEffect(StatusEffects.NIGHT_VISION);
        if (cur == null || cur.getDuration() < 600) {
            mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1200, 0, false, false, false));
        }
    }

    @Override
    protected void onDisable() {
        if (mc.player != null) mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }
}
