package com.shadowclient.hud.widgets;

import com.shadowclient.core.TargetTracker;
import com.shadowclient.hud.HudWidget;
import com.shadowclient.theme.ThemeManager;
import com.shadowclient.util.Animation;
import com.shadowclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;

import java.util.Locale;

public final class TargetInfoWidget extends HudWidget {
    private final Animation health = new Animation(1f, 9f);

    public TargetInfoWidget(int x, int y) { super("Target Info", x, y); }

    @Override public int width() { return 124; }

    @Override public int height() { return 36; }

    @Override
    public void render(DrawContext ctx, boolean editing) {
        LivingEntity t = TargetTracker.get();
        if (t == null && !editing) return;

        String name = t != null ? t.getName().getString() : "Target";
        float hp = t != null ? t.getHealth() : 20f;
        float max = t != null ? Math.max(1f, t.getMaxHealth()) : 20f;
        double dist = t != null && mc.player != null ? mc.player.distanceTo(t) : 0.0;

        panel(ctx, width(), height());
        int x = getX(), y = getY();
        RenderUtil.text(ctx, name, x + 6, y + 5, ThemeManager.text());
        String d = String.format(Locale.ROOT, "%.1fm", dist);
        RenderUtil.text(ctx, d, x + width() - 6 - RenderUtil.textWidth(d), y + 5, ThemeManager.dim());

        health.set(Math.max(0f, Math.min(1f, hp / max)));
        int barW = width() - 12;
        int fill = Math.round(barW * health.get());
        RenderUtil.roundRect(ctx, x + 6, y + 17, barW, 5, 2, ThemeManager.header());
        if (fill > 0) RenderUtil.gradientH(ctx, x + 6, y + 17, fill, 5, ThemeManager.accent(), ThemeManager.accent2());
        RenderUtil.text(ctx, String.format(Locale.ROOT, "%.1f / %.1f HP", hp, max), x + 6, y + 25, ThemeManager.dim());
    }
}
