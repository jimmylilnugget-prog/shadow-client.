package com.shadowclient.module.impl.movement;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import com.shadowclient.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public final class Speed extends Module {
    private final NumberSetting speed = add(new NumberSetting("Speed", 0.3, 0.1, 1.0, 0.01));

    public Speed() {
        super("Speed", "Boosts horizontal movement speed.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity p = mc.player;
        int forward = (mc.options.forwardKey.isPressed() ? 1 : 0) - (mc.options.backKey.isPressed() ? 1 : 0);
        int strafe = (mc.options.leftKey.isPressed() ? 1 : 0) - (mc.options.rightKey.isPressed() ? 1 : 0);
        if (forward == 0 && strafe == 0) return;

        double yaw = Math.toRadians(p.getYaw());
        double len = Math.sqrt(forward * forward + strafe * strafe);
        double s = speed.get();
        double vx = (-Math.sin(yaw) * forward + Math.cos(yaw) * strafe) / len * s;
        double vz = (Math.cos(yaw) * forward + Math.sin(yaw) * strafe) / len * s;
        Vec3d v = p.getVelocity();
        p.setVelocity(vx, v.y, vz);
    }
}
