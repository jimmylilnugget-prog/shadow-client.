package com.shadowclient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Small 2D drawing helpers built on top of {@link DrawContext}. */
public final class RenderUtil {
    public static final int FONT_H = 9;

    private RenderUtil() {}

    public static void rect(DrawContext c, int x, int y, int w, int h, int color) {
        c.fill(x, y, x + w, y + h, color);
    }

    public static void roundRect(DrawContext c, int x, int y, int w, int h, int r, int color) {
        r = Math.max(0, Math.min(r, Math.min(w, h) / 2));
        if (r == 0) { rect(c, x, y, w, h, color); return; }
        for (int i = 0; i < r; i++) {
            double d = r - i - 0.5;
            int inset = (int) Math.ceil(r - Math.sqrt(r * r - d * d));
            c.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            c.fill(x + inset, y + h - i - 1, x + w - inset, y + h - i, color);
        }
        c.fill(x, y + r, x + w, y + h - r, color);
    }

    public static void border(DrawContext c, int x, int y, int w, int h, int color) {
        c.fill(x, y, x + w, y + 1, color);
        c.fill(x, y + h - 1, x + w, y + h, color);
        c.fill(x, y + 1, x + 1, y + h - 1, color);
        c.fill(x + w - 1, y + 1, x + w, y + h - 1, color);
    }

    public static void gradientH(DrawContext c, int x, int y, int w, int h, int left, int right) {
        for (int i = 0; i < w; i += 2) {
            float t = w <= 1 ? 0f : (float) i / (w - 1);
            c.fill(x + i, y, Math.min(x + i + 2, x + w), y + h, ColorUtil.lerp(left, right, t));
        }
    }

    public static int textWidth(String s) {
        return MinecraftClient.getInstance().textRenderer.getWidth(s);
    }

    public static void text(DrawContext c, String s, int x, int y, int color) {
        c.drawText(MinecraftClient.getInstance().textRenderer, s, x, y, color, true);
    }

    public static void centeredText(DrawContext c, String s, int cx, int y, int color) {
        text(c, s, cx - textWidth(s) / 2, y, color);
    }

    /** Draws text with a per-character gradient; {@code phase} shifts it for a shimmer effect. */
    public static int gradientText(DrawContext c, String s, int x, int y, int c1, int c2, float phase) {
        int cx = x;
        for (int i = 0; i < s.length(); i++) {
            String ch = String.valueOf(s.charAt(i));
            float t = (float) ((Math.sin(phase + i * 0.55) + 1.0) * 0.5);
            text(c, ch, cx, y, ColorUtil.lerp(c1, c2, t));
            cx += textWidth(ch);
        }
        return cx - x;
    }
}
