package com.shadowclient.gui.components;

import com.shadowclient.module.Category;

import java.util.EnumMap;
import java.util.Map;

/** Persistent per-window layout (shared between GUI sessions and saved to the config). */
public final class WindowState {
    private static final Map<Category, WindowState> STATES = new EnumMap<>(Category.class);

    public int x = -1;
    public int y = 12;
    public boolean open = true;

    public static WindowState of(Category c) { return STATES.computeIfAbsent(c, k -> new WindowState()); }
}
