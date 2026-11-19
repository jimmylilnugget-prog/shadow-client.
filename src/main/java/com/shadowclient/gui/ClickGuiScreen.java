package com.shadowclient.gui;

import com.shadowclient.ShadowClient;
import com.shadowclient.gui.components.BarButton;
import com.shadowclient.gui.components.CategoryWindow;
import com.shadowclient.gui.components.ModuleButton;
import com.shadowclient.gui.components.SettingRow;
import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import com.shadowclient.module.impl.misc.ClickGuiModule;
import com.shadowclient.theme.Theme;
import com.shadowclient.theme.ThemeManager;
import com.shadowclient.util.Animation;
import com.shadowclient.util.ColorUtil;
import com.shadowclient.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * The main ClickGUI.
 * Left click = toggle, right click = expand settings, middle click = bind key
 * (Backspace/Delete clears, Esc cancels). Right click a header to collapse it.
 */
public final class ClickGuiScreen extends Screen {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final List<CategoryWindow> windows = new ArrayList<>();
    private final List<BarButton> bar = new ArrayList<>();
    private final Animation open = new Animation(0f, 15f);
    private final long openedAt = System.currentTimeMillis();

    private boolean closing;
    private CategoryWindow dragging;
    private SettingRow activeSlider;
    private ModuleButton binding;
    private ModuleButton tooltipFor;
    private long tooltipSince;

    public ClickGuiScreen() {
        super(Text.literal("Shadow ClickGUI"));
        for (Category c : Category.values()) windows.add(new CategoryWindow(c));
        for (Theme t : ThemeManager.all()) bar.add(new BarButton(t.name(), () -> ThemeManager.set(t.name())));
        bar.add(new BarButton("HUD Editor", () -> mc.setScreen(new HudEditorScreen())));
        bar.add(new BarButton("Save", () -> ShadowClient.config().save()));
        bar.add(new BarButton("Load", () -> ShadowClient.config().load()));
    }

    @Override
    protected void init() {
        int step = Math.min(CategoryWindow.WIDTH + 8, (width - 24) / Math.max(1, windows.size()));
        for (int i = 0; i < windows.size(); i++) {
            var st = windows.get(i).state();
            if (st.x < 0) st.x = 12 + i * step;
        }
    }

    public void setDragging(CategoryWindow w) {
        dragging = w;
        windows.remove(w);
        windows.add(w); // bring to front
    }

    public void setActiveSlider(SettingRow row) { activeSlider = row; }

    public void setBinding(ModuleButton b) { binding = b; }

    private void startClose() { closing = true; open.set(0f); }

    @Override public boolean shouldPause() { return false; }

    @Override public void close() { startClose(); }

    @Override public void removed() { ShadowClient.config().save(); }

    // ------------------------------------------------------------ rendering

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        float o = open.get();
        if (closing && o < 0.03f) { mc.setScreen(null); return; }
        if (!closing) open.set(1f);

        ctx.fill(0, 0, width, height, ColorUtil.argb((int) (130 * o), 0, 0, 0));
        int yOffset = Math.round((1f - o) * -14f);

        for (CategoryWindow w : windows) w.render(ctx, mouseX, mouseY, o, yOffset, binding);

        // Title
        String title = "SHADOW CLIENT";
        int tw = RenderUtil.textWidth(title);
        RenderUtil.gradientText(ctx, title, width / 2 - tw / 2, 4, ThemeManager.accent(), ThemeManager.accent2(),
                (System.currentTimeMillis() % 100000L) / 400f);

        renderBar(ctx, mouseX, mouseY, o);
        renderTooltip(ctx, mouseX, mouseY, o);
    }

    private void renderBar(DrawContext ctx, int mx, int my, float a) {
        int total = -4;
        for (BarButton b : bar) total += b.width() + 4;
        int x = width / 2 - total / 2;
        int y = height - BarButton.HEIGHT - 8 + Math.round((1f - a) * 14f);
        for (BarButton b : bar) {
            boolean active = ThemeManager.current().name().equals(b.label());
            b.render(ctx, x, y, mx, my, a, active);
            x += b.width() + 4;
        }
    }

    private void renderTooltip(DrawContext ctx, int mx, int my, float a) {
        ModuleButton hovered = null;
        for (int i = windows.size() - 1; i >= 0 && hovered == null; i--) {
            if (windows.get(i).contains(mx, my)) hovered = windows.get(i).hoveredButton();
        }
        if (hovered != tooltipFor) { tooltipFor = hovered; tooltipSince = System.currentTimeMillis(); }
        if (hovered == null || dragging != null || System.currentTimeMillis() - tooltipSince < 450) return;

        Module m = hovered.module();
        String text = m.getDescription();
        int w = RenderUtil.textWidth(text) + 10;
        int x = Math.min(mx + 10, width - w - 2), y = my + 10;
        RenderUtil.roundRect(ctx, x - 1, y - 1, w + 2, 15, 3, ColorUtil.mulAlpha(ThemeManager.accent(), a));
        RenderUtil.roundRect(ctx, x, y, w, 13, 3, ColorUtil.mulAlpha(ThemeManager.panel(), a));
        RenderUtil.text(ctx, text, x + 5, y + 3, ColorUtil.mulAlpha(ThemeManager.text(), a));
    }

    // ------------------------------------------------------------ input

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int mx = (int) click.x(), my = (int) click.y(), btn = click.button();
        if (closing) return true;
        if (binding != null) binding = null;

        if (btn == 0) for (BarButton b : bar) if (b.click(mx, my)) return true;

        for (int i = windows.size() - 1; i >= 0; i--) {
            CategoryWindow w = windows.get(i);
            if (w.mouseClicked(mx, my, btn, this)) {
                if (dragging == null && i != windows.size() - 1) { windows.remove(w); windows.add(w); }
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        int mx = (int) click.x(), my = (int) click.y();
        if (dragging != null) dragging.dragTo(mx, my, width, height);
        if (activeSlider != null) activeSlider.dragTo(mx);
        return true;
    }

    @Override
    public boolean mouseReleased(Click click) {
        dragging = null;
        activeSlider = null;
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int key = input.key();
        if (binding != null) {
            if (key == GLFW.GLFW_KEY_BACKSPACE || key == GLFW.GLFW_KEY_DELETE) binding.module().setKey(GLFW.GLFW_KEY_UNKNOWN);
            else if (key != GLFW.GLFW_KEY_ESCAPE) binding.module().setKey(key);
            binding = null;
            return true;
        }
        ClickGuiModule guiModule = ShadowClient.modules().get(ClickGuiModule.class);
        boolean toggleKey = guiModule != null && key == guiModule.getKey()
                && System.currentTimeMillis() - openedAt > 300; // ignore key-repeat from the opening press
        if (key == GLFW.GLFW_KEY_ESCAPE || toggleKey) { startClose(); return true; }
        return true;
    }
}
