package com.shadowclient.hud.widgets;

import com.shadowclient.hud.HudWidget;
import com.shadowclient.theme.ThemeManager;
import com.shadowclient.util.ColorUtil;
import com.shadowclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public final class ArmorWidget extends HudWidget {
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public ArmorWidget(int x, int y) { super("Armor", x, y); }

    @Override public int width() { return 52; }

    @Override public int height() { return SLOTS.length * 18 + 4; }

    @Override
    public void render(DrawContext ctx, boolean editing) {
        panel(ctx, width(), height());
        for (int i = 0; i < SLOTS.length; i++) {
            int ry = getY() + 3 + i * 18;
            ItemStack stack = mc.player != null ? mc.player.getEquippedStack(SLOTS[i]) : ItemStack.EMPTY;
            if (stack.isEmpty()) {
                RenderUtil.border(ctx, getX() + 4, ry, 16, 16, ColorUtil.mulAlpha(ThemeManager.dim(), 0.35f));
                continue;
            }
            ctx.drawItem(stack, getX() + 4, ry);
            if (stack.isDamageable()) {
                int max = stack.getMaxDamage();
                float frac = (max - stack.getDamage()) / (float) max;
                int color = ColorUtil.lerp(0xFFFF4040, 0xFF55FF55, frac);
                RenderUtil.text(ctx, Math.round(frac * 100) + "%", getX() + 23, ry + 4, color);
            }
        }
    }
}
