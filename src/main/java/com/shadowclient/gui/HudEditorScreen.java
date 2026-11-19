package com.shadowclient.gui;

import com.shadowclient.ShadowClient;
import com.shadowclient.gui.components.BarButton;
import com.shadowclient.hud.HudWidget;
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

/** Drag HUD elements around. Right click an element to toggle it. */
public final class HudEditorScreen extends Screen {
    private static final int SNAP = 5;

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final List<BarButton> bar = new ArrayList<>();
    private final Animation open = new Animation(0f, 15f);
    private HudWidget dragging;
    private int dragDX, dragDY;
    private boolean snapX, snapY;

    public HudEditorScreen() {
        super(Text.literal("Shadow HUD Editor"));
        bar.add(new BarButton("Reset", () -> ShadowClient.hud().resetPositions()));
        bar.add(new BarButton("Back", () -> mc.setScreen(new ClickGuiScreen())));
        bar.add(new BarButton("Done", this::close));
    }

    @Override public boolean shouldPause() { return false; }

    @Override public void removed() { ShadowClient.config().save(); }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        open.set(1f);
        float o = open.get();
        ctx.fill(0, 0, width, height, ColorUtil.argb((int) (110 * o), 0, 0, 0));

        // centre guides appear while snapping
        int guide = ColorUtil.mulAlpha(ThemeManager.accent(), 0.6f * o);
        if (snapX) ctx.fill(width / 2, 0, width / 2 + 1, height, guide);
        if (snapY) ctx.fill(0, height / 2, width, height / 2 + 1, guide);

        for (HudWidget w : ShadowClient.hud().widgets()) {
            w.clamp(width, height);
            boolean on = w.isEnabled();
            boolean hover = w.contains(mouseX, mouseY) || w == dragging;
            w.render(ctx, true);
            if (!on) {
                RenderUtil.roundRect(ctx, w.getX(), w.getY(), w.width(), w.height(), 3, 0xAA000000);
                RenderUtil.centeredText(ctx, "OFF", w.getX() + w.width() / 2, w.getY() + w.height() / 2 - 4,
                        ThemeManager.dim());
            }
            int outline = hover ? ThemeManager.accent() : ColorUtil.mulAlpha(ThemeManager.dim(), 0.55f);
            RenderUtil.border(ctx, w.getX() - 1, w.getY() - 1, w.width() + 2, w.height() + 2, outline);
            if (hover) RenderUtil.text(ctx, w.getName(), w.getX(), w.getY() - 11, ThemeManager.text());
        }

        RenderUtil.centeredText(ctx, "HUD EDITOR  -  drag to move, right click to toggle", width / 2, 6, ThemeManager.dim());

        int total = -4;
        for (BarButton b : bar) total += b.width() + 4;
        int x = width / 2 - total / 2, y = height - BarButton.HEIGHT - 8;
        for (BarButton b : bar) { b.render(ctx, x, y, mouseX, mouseY, o, false); x += b.width() + 4; }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int mx = (int) click.x(), my = (int) click.y();
        if (click.button() == 0) for (BarButton b : bar) if (b.click(mx, my)) return true;

        List<HudWidget> list = ShadowClient.hud().widgets();
        for (int i = list.size() - 1; i >= 0; i--) {
            HudWidget w = list.get(i);
            if (!w.contains(mx, my)) continue;
            if (click.button() == 0) { dragging = w; dragDX = mx - w.getX(); dragDY = my - w.getY(); }
            else if (click.button() == 1) w.setEnabled(!w.isEnabled());
            return true;
        }
        return true;
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (dragging == null) return true;
        int nx = (int) click.x() - dragDX;
        int ny = (int) click.y() - dragDY;
        int w = dragging.width(), h = dragging.height();

        snapX = Math.abs(nx + w / 2 - width / 2) <= SNAP;
        snapY = Math.abs(ny + h / 2 - height / 2) <= SNAP;
        if (snapX) nx = width / 2 - w / 2;
        if (snapY) ny = height / 2 - h / 2;
        if (Math.abs(nx) <= SNAP) nx = 0;
        if (Math.abs(ny) <= SNAP) ny = 0;
        if (Math.abs(nx + w - width) <= SNAP) nx = width - w;
        if (Math.abs(ny + h - height) <= SNAP) ny = height - h;

        dragging.setPosition(nx, ny);
        return true;
    }

    @Override
    public boolean mouseReleased(Click click) {
        dragging = null;
        snapX = snapY = false;
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == GLFW.GLFW_KEY_ESCAPE) { close(); return true; }
        return true;
    }
}
