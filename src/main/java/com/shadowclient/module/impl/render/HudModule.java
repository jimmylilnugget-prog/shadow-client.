package com.shadowclient.module.impl.render;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import com.shadowclient.module.setting.BooleanSetting;

/** Master switch for every HUD element. Individual elements are toggled in the HUD editor. */
public final class HudModule extends Module {
    private final BooleanSetting background = add(new BooleanSetting("Background", true));
    private final BooleanSetting shimmer = add(new BooleanSetting("Animated Watermark", true));

    public HudModule() {
        super("HUD", "Master switch for the Shadow HUD overlay.", Category.RENDER);
        setEnabledQuiet(true);
    }

    public boolean background() { return background.get(); }

    public boolean shimmer() { return shimmer.get(); }
}
