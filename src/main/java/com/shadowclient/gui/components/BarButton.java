package com.shadowclient.gui.components;

import com.shadowclient.theme.ThemeManager;
import com.shadowclient.util.Animation;
import com.shadowclient.util.ColorUtil;
import com.shadowclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

/** Small pill button used in the bottom bars of the ClickGUI and HUD editor. */
public final class BarButton {
    public static final int HEIGHT = 15;

    private final String label;
    private final Runnable action;
    private final Animation hover = new Animation(0f, 18f);
    private int x, y, w;

    public BarButton(String label, Runnable action) {
        this.label = label;
        this.action = action;
    }

    public String label() { return label; }

    public int width() { return RenderUtil.textWidth(label) + 16; }

    public void render(DrawContext ctx, int x, int y, int mx, int my, float alpha, boolean active) {
        this.x = x; this.y = y; this.w = width();
        hover.set(contains(mx, my) ? 1f : 0f);
        float h = hover.get();
        int bg = ColorUtil.lerp(ThemeManager.panel(), ThemeManager.header(), 0.6f + 0.4f * h);
        RenderUtil.roundRect(ctx, x - 1, y - 1, w + 2, HEIGHT + 2, 3,
                ColorUtil.mulAlpha(active ? ThemeManager.accent() : ThemeManager.outline(), alpha));
        RenderUtil.roundRect(ctx, x, y, w, HEIGHT, 3, ColorUtil.mulAlpha(bg, alpha));
        int tc = ColorUtil.lerp(ThemeManager.dim(), active ? ThemeManager.accent() : ThemeManager.text(), Math.max(h, active ? 1f : 0f));
        RenderUtil.text(ctx, label, x + 8, y + 4, ColorUtil.mulAlpha(tc, alpha));
    }

    public boolean contains(int mx, int my) { return mx >= x && mx < x + w && my >= y && my < y + HEIGHT; }

    public boolean click(int mx, int my) {
        if (!contains(mx, my)) return false;
        action.run();
        return true;
    }
}
