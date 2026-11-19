package com.shadowclient.module.impl.player;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

public final class AutoTool extends Module {
    public AutoTool() {
        super("AutoTool", "Selects the fastest hotbar tool for the block you mine.", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (!mc.options.attackKey.isPressed()) return;
        if (!(mc.crosshairTarget instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) return;
        BlockState state = mc.world.getBlockState(hit.getBlockPos());
        PlayerInventory inv = mc.player.getInventory();
        int best = -1;
        float bestSpeed = 1.0f;
        for (int i = 0; i < 9; i++) {
            float s = inv.getStack(i).getMiningSpeedMultiplier(state);
            if (s > bestSpeed) { bestSpeed = s; best = i; }
        }
        if (best != -1) inv.setSelectedSlot(best);
    }
}
