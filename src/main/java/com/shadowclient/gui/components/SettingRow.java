package com.shadowclient.gui.components;

import com.shadowclient.module.setting.BooleanSetting;
import com.shadowclient.module.setting.ModeSetting;
import com.shadowclient.module.setting.NumberSetting;
import com.shadowclient.module.setting.Setting;
import com.shadowclient.theme.ThemeManager;
import com.shadowclient.util.Animation;
import com.shadowclient.util.ColorUtil;
import com.shadowclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.util.Locale;

/** One editable setting inside an expanded module button. */
public final class SettingRow {
    private static final int PAD = 7;

    private final Setting<?> setting;
    private final Animation anim = new Animation(0f, 16f);
    private int x, y, w;

    public SettingRow(Setting<?> setting) {
        this.setting = setting;
        if (setting instanceof BooleanSetting b) anim.snap(b.get() ? 1f : 0f);
        if (setting instanceof NumberSetting n) anim.snap(pct(n));
    }

    public boolean isSlider() { return setting instanceof NumberSetting; }

    public int height() { return isSlider() ? 20 : 14; }

    private static float pct(NumberSetting n) { return (float) ((n.get() - n.min()) / (n.max() - n.min())); }

    public void render(DrawContext ctx, int x, int y, int w, int mx, int my, float a) {
        this.x = x; this.y = y; this.w = w;
        int h = height();
        ctx.fill(x, y, x + w, y + h, ColorUtil.mulAlpha(0x55000000, a));
        int text = ColorUtil.mulAlpha(ThemeManager.text(), a);
        int accent = ColorUtil.mulAlpha(ThemeManager.accent(), a);

        if (setting instanceof BooleanSetting b) {
            anim.set(b.get() ? 1f : 0f);
            float t = anim.get();
            RenderUtil.text(ctx, b.getName(), x + PAD, y + 3, text);
            int tx = x + w - PAD - 16;
            RenderUtil.roundRect(ctx, tx, y + 3, 16, 8, 4,
                    ColorUtil.lerp(ColorUtil.mulAlpha(ThemeManager.header(), a), accent, t));
            RenderUtil.roundRect(ctx, tx + 1 + Math.round(8 * t), y + 4, 6, 6, 3, ColorUtil.mulAlpha(0xFFFFFFFF, a));
        } else if (setting instanceof NumberSetting n) {
            anim.set(pct(n));
            float t = anim.get();
            RenderUtil.text(ctx, n.getName(), x + PAD, y + 3, text);
            String v = n.step() >= 1 ? String.valueOf(n.intValue()) : String.format(Locale.ROOT, "%.2f", n.get());
            RenderUtil.text(ctx, v, x + w - PAD - RenderUtil.textWidth(v), y + 3, accent);
            int tw = trackW();
            RenderUtil.roundRect(ctx, x + PAD, y + 14, tw, 3, 1, ColorUtil.mulAlpha(ThemeManager.header(), a));
            int fill = Math.max(2, Math.round(tw * t));
            RenderUtil.gradientH(ctx, x + PAD, y + 14, fill, 3,
                    ColorUtil.mulAlpha(ThemeManager.accent2(), a), accent);
            RenderUtil.roundRect(ctx, x + PAD + fill - 2, y + 12, 4, 7, 1, ColorUtil.mulAlpha(0xFFFFFFFF, a));
        } else if (setting instanceof ModeSetting m) {
            RenderUtil.text(ctx, m.getName(), x + PAD, y + 3, text);
            RenderUtil.text(ctx, m.get(), x + w - PAD - RenderUtil.textWidth(m.get()), y + 3, accent);
        }
    }

    private int trackW() { return w - PAD * 2; }

    public boolean mouseClicked(int mx, int my, int button) {
        if (mx < x || mx >= x + w || my < y || my >= y + height()) return false;
        if (setting instanceof BooleanSetting b && button == 0) b.toggle();
        else if (setting instanceof ModeSetting m) m.cycle(button == 1 ? -1 : 1);
        else if (setting instanceof NumberSetting) dragTo(mx);
        return true;
    }

    public void dragTo(int mx) {
        if (!(setting instanceof NumberSetting n)) return;
        double p = Math.max(0.0, Math.min(1.0, (mx - (x + PAD)) / (double) trackW()));
        n.set(n.min() + p * (n.max() - n.min()));
    }
}
