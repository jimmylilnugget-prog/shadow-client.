package com.shadowclient.module;

import com.shadowclient.ShadowClient;
import com.shadowclient.module.setting.Setting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for every feature. Modules are state driven: {@link #onTick()} runs every client
 * tick while enabled, so a module restored from a saved config needs no special start-up code.
 */
public abstract class Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private final String description;
    private final Category category;
    private final List<Setting<?>> settings = new ArrayList<>();
    private int key = GLFW.GLFW_KEY_UNKNOWN;
    private boolean enabled;

    protected Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    protected <S extends Setting<?>> S add(S setting) {
        settings.add(setting);
        return setting;
    }

    public void toggle() { setEnabled(!enabled); }

    public void setEnabled(boolean value) {
        if (value == enabled) return;
        enabled = value;
        if (value) onEnable(); else onDisable();
        ShadowClient.modules().notifyToggle(this);
    }

    /** Changes the state without firing callbacks or notifications (used by config loading). */
    public void setEnabledQuiet(boolean value) { enabled = value; }

    protected void onEnable() {}

    protected void onDisable() {}

    public void onTick() {}

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; }
    public List<Setting<?>> getSettings() { return Collections.unmodifiableList(settings); }
}
