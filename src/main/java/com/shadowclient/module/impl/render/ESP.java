package com.shadowclient.module.impl.render;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import com.shadowclient.module.setting.BooleanSetting;
import com.shadowclient.module.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashSet;
import java.util.Set;

/** Outlines entities through walls using the vanilla glowing effect (client-side only). */
public final class ESP extends Module {
    private final NumberSetting range = add(new NumberSetting("Range", 64, 8, 128, 4));
    private final BooleanSetting players = add(new BooleanSetting("Players", true));
    private final BooleanSetting mobs = add(new BooleanSetting("Mobs", true));
    private final Set<Entity> glowing = new HashSet<>();

    public ESP() {
        super("ESP", "Highlights nearby players and mobs through walls.", Category.RENDER);
    }

    @Override
    public void onTick() {
        Set<Entity> now = new HashSet<>();
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity) || e == mc.player) continue;
            boolean match = (players.get() && e instanceof PlayerEntity) || (mobs.get() && e instanceof Monster);
            if (match && mc.player.distanceTo(e) <= range.get()) {
                e.setGlowing(true);
                now.add(e);
            }
        }
        for (Entity old : glowing) if (!now.contains(old)) old.setGlowing(false);
        glowing.clear();
        glowing.addAll(now);
    }

    @Override
    protected void onDisable() {
        for (Entity e : glowing) e.setGlowing(false);
        glowing.clear();
    }
}
