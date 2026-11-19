package com.shadowclient.hud;

import com.shadowclient.ShadowClient;
import com.shadowclient.module.impl.render.HudModule;
import com.shadowclient.theme.ThemeManager;
import com.shadowclient.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** A movable HUD element. Positions are in scaled GUI coordinates. */
public abstract class HudWidget {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private int x;
    private int y;
    private boolean enabled = true;

    protected HudWidget(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public abstract int width();

    public abstract int height();

    /** @param editing true when drawn as a preview inside the HUD editor. */
    public abstract void render(DrawContext ctx, boolean editing);

    public String getName() { return name; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean contains(double mx, double my) {
        return mx >= x && mx <= x + width() && my >= y && my <= y + height();
    }

    public void clamp(int screenW, int screenH) {
        x = Math.max(0, Math.min(x, Math.max(0, screenW - width())));
        y = Math.max(0, Math.min(y, Math.max(0, screenH - height())));
    }

    /** Draws the shared widget background with a thin accent line on top. */
    protected void panel(DrawContext ctx, int w, int h) {
        HudModule hm = ShadowClient.modules().get(HudModule.class);
        if (hm != null && !hm.background()) return;
        RenderUtil.roundRect(ctx, x, y, w, h, 3, ThemeManager.background());
        ctx.fill(x + 2, y, x + w - 2, y + 1, ThemeManager.accent());
    }
}
