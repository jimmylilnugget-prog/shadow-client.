package com.shadowclient.hud.widgets;

import com.shadowclient.hud.HudWidget;
import com.shadowclient.theme.ThemeManager;
import com.shadowclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

/** Compact "LABEL value" pill used by simple counters. */
abstract class LabelWidget extends HudWidget {
    protected LabelWidget(String name, int x, int y) { super(name, x, y); }

    protected abstract String label();

    protected abstract String value(boolean editing);

    @Override
    public int width() { return Math.max(46, RenderUtil.textWidth(label() + " " + value(false)) + 12); }

    @Override
    public int height() { return 15; }

    @Override
    public void render(DrawContext ctx, boolean editing) {
        panel(ctx, width(), height());
        int tx = getX() + 6;
        RenderUtil.text(ctx, label(), tx, getY() + 4, ThemeManager.accent());
        RenderUtil.text(ctx, value(editing), tx + RenderUtil.textWidth(label() + " "), getY() + 4, ThemeManager.text());
    }
}
