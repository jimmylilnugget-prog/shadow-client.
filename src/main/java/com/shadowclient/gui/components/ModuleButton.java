package com.shadowclient.gui.components;

import com.shadowclient.gui.ClickGuiScreen;
import com.shadowclient.module.Module;
import com.shadowclient.module.setting.Setting;
import com.shadowclient.theme.ThemeManager;
import com.shadowclient.util.Animation;
import com.shadowclient.util.ColorUtil;
import com.shadowclient.util.KeyNames;
import com.shadowclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

/** A toggleable module row that can expand to show its settings. */
public final class ModuleButton {
    public static final int HEIGHT = 15;

    private final Module module;
    private final List<SettingRow> rows = new ArrayList<>();
    private final Animation hover = new Animation(0f, 18f);
    private final Animation enable;
    private final Animation expand = new Animation(0f, 14f);
    private boolean expanded;
    private float expandValue;
    private boolean hovered;

    public ModuleButton(Module module) {
        this.module = module;
        this.enable = new Animation(module.isEnabled() ? 1f : 0f, 14f);
        for (Setting<?> s : module.getSettings()) rows.add(new SettingRow(s));
    }

    public Module module() { return module; }

    public boolean isHovered() { return hovered; }

    private int rowsHeight() {
        int h = 0;
        for (SettingRow r : rows) h += r.height();
        return h;
    }

    /** Must be called once per frame before {@link #totalHeight()}. */
    public void update() { expandValue = expand.get(); }

    public int totalHeight() { return HEIGHT + Math.round(rowsHeight() * expandValue); }

    public void render(DrawContext ctx, int x, int y, int w, int mx, int my, float a, boolean listening) {
        hovered = mx >= x && mx < x + w && my >= y && my < y + HEIGHT;
        hover.set(hovered ? 1f : 0f);
        enable.set(module.isEnabled() ? 1f : 0f);
        float h = hover.get();
        float e = enable.get();

        ctx.fill(x, y, x + w, y + HEIGHT, ColorUtil.mulAlpha(ColorUtil.lerp(ThemeManager.panel(), ThemeManager.header(), h), a));
        if (e > 0.01f) {
            RenderUtil.gradientH(ctx, x, y, w, HEIGHT,
                    ColorUtil.mulAlpha(ThemeManager.accent(), 0.30f * e * a),
                    ColorUtil.mulAlpha(ThemeManager.accent2(), 0.06f * e * a));
            ctx.fill(x, y, x + 2, y + HEIGHT, ColorUtil.mulAlpha(ThemeManager.accent(), e * a));
        }

        int nameColor = ColorUtil.lerp(ColorUtil.lerp(ThemeManager.dim(), ThemeManager.text(), h), ThemeManager.accent(), e);
        RenderUtil.text(ctx, module.getName(), x + 7 + Math.round(2 * h), y + 4, ColorUtil.mulAlpha(nameColor, a));

        int right = x + w - 6;
        if (!rows.isEmpty()) {
            String sign = expanded ? "-" : "+";
            right -= RenderUtil.textWidth(sign);
            RenderUtil.text(ctx, sign, right, y + 4, ColorUtil.mulAlpha(ThemeManager.dim(), a));
            right -= 5;
        }
        String key = listening ? "[...]" : module.getKey() > 0 ? "[" + KeyNames.of(module.getKey()) + "]" : "";
        if (!key.isEmpty() && (hovered || listening || module.getKey() > 0)) {
            right -= RenderUtil.textWidth(key);
            RenderUtil.text(ctx, key, right, y + 4, ColorUtil.mulAlpha(listening ? ThemeManager.accent() : ThemeManager.dim(), a));
        }

        if (expandValue > 0.01f) {
            int visible = Math.round(rowsHeight() * expandValue);
            ctx.enableScissor(x, y + HEIGHT, x + w, y + HEIGHT + visible);
            int ry = y + HEIGHT;
            for (SettingRow r : rows) {
                r.render(ctx, x, ry, w, mx, my, a);
                ry += r.height();
            }
            ctx.disableScissor();
        }
    }

    /** @param top the y coordinate this button was rendered at. */
    public boolean mouseClicked(int mx, int my, int button, int top, ClickGuiScreen gui) {
        if (my < top + HEIGHT) {
            if (button == 0) module.toggle();
            else if (button == 1 && !rows.isEmpty()) { expanded = !expanded; expand.set(expanded ? 1f : 0f); }
            else if (button == 2) gui.setBinding(this);
            return true;
        }
        if (expandValue < 0.99f) return true;
        int ry = top + HEIGHT;
        for (SettingRow r : rows) {
            if (r.mouseClicked(mx, my, button)) {
                if (r.isSlider()) gui.setActiveSlider(r);
                return true;
            }
            ry += r.height();
        }
        return true;
    }
}
