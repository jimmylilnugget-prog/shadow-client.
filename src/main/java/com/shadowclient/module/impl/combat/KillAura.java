package com.shadowclient.module.impl.combat;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import com.shadowclient.module.setting.BooleanSetting;
import com.shadowclient.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public final class KillAura extends Module {
    private final NumberSetting range = add(new NumberSetting("Range", 4.2, 2.0, 6.0, 0.1));
    private final BooleanSetting players = add(new BooleanSetting("Players", true));
    private final BooleanSetting mobs = add(new BooleanSetting("Mobs", true));
    private final BooleanSetting animals = add(new BooleanSetting("Animals", false));
    private final BooleanSetting rotate = add(new BooleanSetting("Rotate", true));
    private LivingEntity target;

    public KillAura() {
        super("KillAura", "Attacks the closest valid target in range.", Category.COMBAT);
    }

    public LivingEntity getTarget() { return isEnabled() ? target : null; }

    @Override
    protected void onDisable() { target = null; }

    @Override
    public void onTick() {
        target = null;
        ClientPlayerEntity p = mc.player;
        double best = range.get();
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity le) || le == p || !le.isAlive() || !isValid(le)) continue;
            double d = p.distanceTo(le);
            if (d <= best) { best = d; target = le; }
        }
        if (target == null) return;
        if (rotate.get()) face(p, target);
        if (p.getAttackCooldownProgress(0.5f) >= 1.0f) {
            mc.interactionManager.attackEntity(p, target);
            p.swingHand(Hand.MAIN_HAND);
        }
    }

    private boolean isValid(LivingEntity e) {
        if (e instanceof PlayerEntity) return players.get();
        if (e instanceof Monster) return mobs.get();
        if (e instanceof AnimalEntity) return animals.get();
        return false;
    }

    private static void face(ClientPlayerEntity p, LivingEntity e) {
        Vec3d eye = p.getEyePos();
        Vec3d t = e.getBoundingBox().getCenter();
        double dx = t.x - eye.x, dy = t.y - eye.y, dz = t.z - eye.z;
        double flat = Math.sqrt(dx * dx + dz * dz);
        p.setYaw((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
        p.setPitch((float) -Math.toDegrees(Math.atan2(dy, flat)));
    }
}
