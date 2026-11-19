package com.shadowclient.hud.widgets;

import com.shadowclient.ShadowClient;
import com.shadowclient.hud.HudWidget;
import com.shadowclient.module.impl.render.HudModule;
import com.shadowclient.theme.ThemeManager;
import com.shadowclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

public final class WatermarkWidget extends HudWidget {
    private static final String TITLE = "Shadow";

    public WatermarkWidget(int x, int y) { super("Watermark", x, y); }

    private String tail() {
        String user = mc.getSession() != null ? mc.getSession().getUsername() : "player";
        return " Client  |  v" + ShadowClient.VERSION + "  |  " + user;
    }

    @Override
    public int width() { return RenderUtil.textWidth(TITLE) + RenderUtil.textWidth(tail()) + 14; }

    @Override
    public int height() { return 17; }

    @Override
    public void render(DrawContext ctx, boolean editing) {
        panel(ctx, width(), height());
        HudModule hm = ShadowClient.modules().get(HudModule.class);
        boolean shimmer = hm == null || hm.shimmer();
        float phase = shimmer ? (System.currentTimeMillis() % 100000L) / 400f : 0f;
        int tx = getX() + 7, ty = getY() + 5;
        int w = RenderUtil.gradientText(ctx, TITLE, tx, ty, ThemeManager.accent(), ThemeManager.accent2(), phase);
        RenderUtil.text(ctx, tail(), tx + w, ty, ThemeManager.dim());
    }
}
