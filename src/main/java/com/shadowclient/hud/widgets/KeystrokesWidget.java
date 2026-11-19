package com.shadowclient.hud.widgets;

import com.shadowclient.hud.HudWidget;
import com.shadowclient.theme.ThemeManager;
import com.shadowclient.util.Animation;
import com.shadowclient.util.ColorUtil;
import com.shadowclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.GameOptions;

public final class KeystrokesWidget extends HudWidget {
    private static final int KEY = 20;
    private static final int GAP = 2;
    private final Animation[] anims = new Animation[7];

    public KeystrokesWidget(int x, int y) {
        super("Keystrokes", x, y);
        for (int i = 0; i < anims.length; i++) anims[i] = new Animation(0f, 20f);
    }

    @Override public int width() { return KEY * 3 + GAP * 2; }

    @Override public int height() { return (KEY + GAP) * 3 + 12; }

    @Override
    public void render(DrawContext ctx, boolean editing) {
        GameOptions o = mc.options;
        int x = getX(), y = getY();
        int step = KEY + GAP;
        int half = (width() - GAP) / 2;
        cell(ctx, 0, "W", x + step, y, KEY, KEY, !editing && o.forwardKey.isPressed());
        cell(ctx, 1, "A", x, y + step, KEY, KEY, !editing && o.leftKey.isPressed());
        cell(ctx, 2, "S", x + step, y + step, KEY, KEY, !editing && o.backKey.isPressed());
        cell(ctx, 3, "D", x + step * 2, y + step, KEY, KEY, !editing && o.rightKey.isPressed());
        cell(ctx, 4, "LMB", x, y + step * 2, half, KEY, !editing && o.attackKey.isPressed());
        cell(ctx, 5, "RMB", x + half + GAP, y + step * 2, half, KEY, !editing && o.useKey.isPressed());
        cell(ctx, 6, "SPACE", x, y + step * 3, width(), 12, !editing && o.jumpKey.isPressed());
    }

    private void cell(DrawContext ctx, int i, String label, int x, int y, int w, int h, boolean down) {
        anims[i].set(down ? 1f : 0f);
        float t = anims[i].get();
        int bg = ColorUtil.lerp(ThemeManager.background(), ColorUtil.mulAlpha(ThemeManager.accent(), 0.85f), t);
        RenderUtil.roundRect(ctx, x, y, w, h, 3, bg);
        int color = ColorUtil.lerp(ThemeManager.text(), 0xFF0B0B0F, t);
        RenderUtil.centeredText(ctx, label, x + w / 2, y + (h - RenderUtil.FONT_H) / 2 + 1, color);
    }
}
