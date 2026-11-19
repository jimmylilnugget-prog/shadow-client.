package com.shadowclient.hud;

import com.shadowclient.ShadowClient;
import com.shadowclient.gui.HudEditorScreen;
import com.shadowclient.hud.widgets.ArmorWidget;
import com.shadowclient.hud.widgets.CoordsWidget;
import com.shadowclient.hud.widgets.CpsWidget;
import com.shadowclient.hud.widgets.FpsWidget;
import com.shadowclient.hud.widgets.KeystrokesWidget;
import com.shadowclient.hud.widgets.TargetInfoWidget;
import com.shadowclient.hud.widgets.WatermarkWidget;
import com.shadowclient.module.impl.render.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HudManager {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final List<HudWidget> widgets = new ArrayList<>();

    public HudManager() {
        resetPositions();
    }

    public List<HudWidget> widgets() { return Collections.unmodifiableList(widgets); }

    public HudWidget byName(String name) {
        for (HudWidget w : widgets) if (w.getName().equalsIgnoreCase(name)) return w;
        return null;
    }

    /** (Re)creates all widgets at their default positions. */
    public void resetPositions() {
        int sw = Math.max(320, mc.getWindow() != null ? mc.getWindow().getScaledWidth() : 480);
        int sh = Math.max(180, mc.getWindow() != null ? mc.getWindow().getScaledHeight() : 270);
        widgets.clear();
        widgets.add(new WatermarkWidget(6, 6));
        widgets.add(new FpsWidget(6, 26));
        widgets.add(new CpsWidget(6, 44));
        widgets.add(new CoordsWidget(6, sh - 46));
        widgets.add(new KeystrokesWidget(6, sh / 2 - 40));
        widgets.add(new ArmorWidget(sw - 58, sh / 2 - 36));
        widgets.add(new TargetInfoWidget(sw / 2 + 12, sh / 2 + 24));
    }

    public void render(DrawContext ctx) {
        if (mc.options.hudHidden || mc.player == null) return;
        HudModule hm = ShadowClient.modules().get(HudModule.class);
        if (hm == null || !hm.isEnabled()) return;
        if (mc.currentScreen instanceof HudEditorScreen) return;

        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        for (HudWidget w : widgets) {
            if (!w.isEnabled()) continue;
            w.clamp(sw, sh);
            w.render(ctx, false);
        }
    }
}
