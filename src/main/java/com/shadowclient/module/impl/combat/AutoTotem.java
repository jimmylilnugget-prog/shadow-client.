package com.shadowclient.module.impl.combat;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import com.shadowclient.module.setting.NumberSetting;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public final class AutoTotem extends Module {
    private final NumberSetting delay = add(new NumberSetting("Delay (ticks)", 2, 0, 20, 1));
    private int timer;

    public AutoTotem() {
        super("AutoTotem", "Keeps a Totem of Undying in your offhand.", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.currentScreen != null) return;
        if (mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) return;
        if (timer++ < delay.intValue()) return;
        for (int i = 0; i < 36; i++) {
            if (!mc.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) continue;
            int screenSlot = i < 9 ? 36 + i : i; // hotbar sits at 36-44 in the player screen handler
            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, screenSlot, 40,
                    SlotActionType.SWAP, mc.player);
            timer = 0;
            return;
        }
    }
}
