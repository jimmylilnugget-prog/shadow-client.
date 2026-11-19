package com.shadowclient.theme;

import com.shadowclient.util.Animation;
import com.shadowclient.util.ColorUtil;

import java.util.List;
import java.util.function.ToIntFunction;

/** Holds the active theme and cross-fades colours when it changes. */
public final class ThemeManager {
    private static final List<Theme> THEMES = List.of(Theme.DARK, Theme.RED, Theme.PURPLE);
    private static final Animation BLEND = new Animation(1f, 7f);
    private static Theme from = Theme.DARK;
    private static Theme current = Theme.DARK;

    private ThemeManager() {}

    public static List<Theme> all() { return THEMES; }

    public static Theme current() { return current; }

    public static void set(String name) {
        for (Theme t : THEMES) {
            if (t.name().equalsIgnoreCase(name) && t != current) {
                from = current;
                current = t;
                BLEND.snap(0f);
                BLEND.set(1f);
                return;
            }
        }
    }

    private static int mix(ToIntFunction<Theme> f) {
        return ColorUtil.lerp(f.applyAsInt(from), f.applyAsInt(current), BLEND.get());
    }

    public static int background() { return mix(Theme::background); }
    public static int panel() { return mix(Theme::panel); }
    public static int header() { return mix(Theme::header); }
    public static int accent() { return mix(Theme::accent); }
    public static int accent2() { return mix(Theme::accent2); }
    public static int text() { return mix(Theme::text); }
    public static int dim() { return mix(Theme::dim); }
    public static int outline() { return mix(Theme::outline); }
}
