package com.shadowclient.gui.components;

import com.shadowclient.ShadowClient;
import com.shadowclient.gui.ClickGuiScreen;
import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import com.shadowclient.theme.ThemeManager;
import com.shadowclient.util.Animation;
import com.shadowclient.util.ColorUtil;
import com.shadowclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

/** A draggable, collapsible window listing the modules of one category. */
public final class CategoryWindow {
    public static final int WIDTH = 108;
    public static final int HEADER = 17;

    private final Category category;
    private final WindowState state;
    private final List<ModuleButton> buttons = new ArrayList<>();
    private final Animation open;
    private int rx, ry, rh;
    private int dragDX, dragDY;

    public CategoryWindow(Category category) {
        this.category = category;
        this.state = WindowState.of(category);
        this.open = new Animation(state.open ? 1f : 0f, 14f);
        for (Module m : ShadowClient.modules().inCategory(category)) buttons.add(new ModuleButton(m));
    }

    public WindowState state() { return state; }

    public ModuleButton hoveredButton() {
        for (ModuleButton b : buttons) if (b.isHovered()) return b;
        return null;
    }

    public void render(DrawContext ctx, int mx, int my, float a, int yOffset, ModuleButton binding) {
        open.set(state.open ? 1f : 0f);
        float o = open.get();
        int full = 0;
        for (ModuleButton b : buttons) { b.update(); full += b.totalHeight(); }
        int bodyH = Math.round(full * o);

        int x = state.x, y = state.y + yOffset, h = HEADER + bodyH;
        rx = x; ry = y; rh = h;

        RenderUtil.roundRect(ctx, x - 1, y - 1, WIDTH + 2, h + 2, 3, ColorUtil.mulAlpha(ThemeManager.outline(), a));
        RenderUtil.roundRect(ctx, x, y, WIDTH, h, 3, ColorUtil.mulAlpha(ThemeManager.panel(), a));

        RenderUtil.roundRect(ctx, x, y, WIDTH, HEADER, 3, ColorUtil.mulAlpha(ThemeManager.header(), a));
        ctx.fill(x, y + HEADER - 4, x + WIDTH, y + HEADER, ColorUtil.mulAlpha(ThemeManager.header(), a));
        RenderUtil.gradientH(ctx, x + 2, y + HEADER - 1, WIDTH - 4, 1,
                ColorUtil.mulAlpha(ThemeManager.accent(), a), ColorUtil.mulAlpha(ThemeManager.accent2(), a));
        ctx.fill(x + 4, y + 4, x + 6, y + 13, ColorUtil.mulAlpha(ThemeManager.accent(), a));
        RenderUtil.text(ctx, category.display(), x + 10, y + 5, ColorUtil.mulAlpha(ThemeManager.text(), a));
        String count = String.valueOf(buttons.size());
        RenderUtil.text(ctx, count, x + WIDTH - 6 - RenderUtil.textWidth(count), y + 5,
                ColorUtil.mulAlpha(ThemeManager.dim(), a));

        if (bodyH > 0) {
            ctx.enableScissor(x, y + HEADER, x + WIDTH, y + HEADER + bodyH);
            int by = y + HEADER;
            for (ModuleButton b : buttons) {
                b.render(ctx, x, by, WIDTH, mx, my, a, b == binding);
                by += b.totalHeight();
            }
            ctx.disableScissor();
        }
    }

    public boolean contains(int mx, int my) {
        return mx >= rx && mx < rx + WIDTH && my >= ry && my < ry + rh;
    }

    /** @return true if the click was consumed by this window. */
    public boolean mouseClicked(int mx, int my, int button, ClickGuiScreen gui) {
        if (!contains(mx, my)) return false;
        if (my < ry + HEADER) {
            if (button == 0) { dragDX = mx - state.x; dragDY = my - state.y; gui.setDragging(this); }
            else if (button == 1) state.open = !state.open;
            return true;
        }
        int by = ry + HEADER;
        for (ModuleButton b : buttons) {
            int bh = b.totalHeight();
            if (my >= by && my < by + bh) return b.mouseClicked(mx, my, button, by, gui);
            by += bh;
        }
        return true;
    }

    public void dragTo(int mx, int my, int screenW, int screenH) {
        state.x = Math.max(0, Math.min(mx - dragDX, screenW - WIDTH));
        state.y = Math.max(0, Math.min(my - dragDY, screenH - HEADER));
    }
}
