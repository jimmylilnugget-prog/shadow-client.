package com.shadowclient.hud.widgets;

import com.shadowclient.hud.HudWidget;
import com.shadowclient.theme.ThemeManager;
import com.shadowclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.world.World;

import java.util.Locale;

public final class CoordsWidget extends HudWidget {
    public CoordsWidget(int x, int y) { super("Coordinates", x, y); }

    private String[] values() {
        if (mc.player == null || mc.world == null) {
            return new String[]{"0.0  0.0  0.0", "0, 0", "North"};
        }
        double x = mc.player.getX(), y = mc.player.getY(), z = mc.player.getZ();
        boolean nether = mc.world.getRegistryKey() == World.NETHER;
        boolean overworld = mc.world.getRegistryKey() == World.OVERWORLD;
        String other = "-";
        if (overworld) other = (int) Math.floor(x / 8) + ", " + (int) Math.floor(z / 8);
        else if (nether) other = (int) Math.floor(x * 8) + ", " + (int) Math.floor(z * 8);
        String dir = mc.player.getHorizontalFacing().asString();
        dir = Character.toUpperCase(dir.charAt(0)) + dir.substring(1);
        return new String[]{String.format(Locale.ROOT, "%.1f  %.1f  %.1f", x, y, z), other, dir};
    }

    private static final String[] LABELS = {"XYZ", "Nether", "Facing"};

    @Override
    public int width() {
        String[] v = values();
        int max = 0;
        for (int i = 0; i < 3; i++) max = Math.max(max, RenderUtil.textWidth(LABELS[i] + "  " + v[i]));
        return Math.max(110, max + 12);
    }

    @Override
    public int height() { return 38; }

    @Override
    public void render(DrawContext ctx, boolean editing) {
        panel(ctx, width(), height());
        String[] v = values();
        String l2 = mc.world != null && mc.world.getRegistryKey() == World.NETHER ? "Overworld" : "Nether";
        for (int i = 0; i < 3; i++) {
            String label = i == 1 ? l2 : LABELS[i];
            int ty = getY() + 5 + i * 11;
            RenderUtil.text(ctx, label, getX() + 6, ty, ThemeManager.accent());
            RenderUtil.text(ctx, v[i], getX() + 6 + RenderUtil.textWidth(label + "  "), ty, ThemeManager.text());
        }
    }
}
