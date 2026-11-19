package com.shadowclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shadowclient.ShadowClient;
import com.shadowclient.core.ModuleManager;
import com.shadowclient.gui.components.WindowState;
import com.shadowclient.hud.HudManager;
import com.shadowclient.hud.HudWidget;
import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import com.shadowclient.module.setting.Setting;
import com.shadowclient.theme.ThemeManager;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Saves and loads modules, keybinds, settings, theme, HUD layout and GUI layout as JSON. */
public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path file = FabricLoader.getInstance().getConfigDir().resolve("shadowclient").resolve("default.json");
    private final ModuleManager modules;
    private final HudManager hud;

    public ConfigManager(ModuleManager modules, HudManager hud) {
        this.modules = modules;
        this.hud = hud;
    }

    public Path path() { return file; }

    public void save() {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("version", ShadowClient.VERSION);
            root.addProperty("theme", ThemeManager.current().name());

            JsonObject mods = new JsonObject();
            for (Module m : modules.all()) {
                JsonObject o = new JsonObject();
                o.addProperty("enabled", m.isEnabled());
                o.addProperty("key", m.getKey());
                JsonObject settings = new JsonObject();
                for (Setting<?> s : m.getSettings()) settings.add(s.getName(), s.serialize());
                o.add("settings", settings);
                mods.add(m.getName(), o);
            }
            root.add("modules", mods);

            JsonObject hudObj = new JsonObject();
            for (HudWidget w : hud.widgets()) {
                JsonObject o = new JsonObject();
                o.addProperty("x", w.getX());
                o.addProperty("y", w.getY());
                o.addProperty("enabled", w.isEnabled());
                hudObj.add(w.getName(), o);
            }
            root.add("hud", hudObj);

            JsonObject gui = new JsonObject();
            for (Category c : Category.values()) {
                WindowState st = WindowState.of(c);
                JsonObject o = new JsonObject();
                o.addProperty("x", st.x);
                o.addProperty("y", st.y);
                o.addProperty("open", st.open);
                gui.add(c.name(), o);
            }
            root.add("gui", gui);

            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(root));
        } catch (IOException | RuntimeException e) {
            ShadowClient.LOGGER.error("Failed to save config", e);
        }
    }

    public void load() {
        if (!Files.exists(file)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(file)).getAsJsonObject();

            if (root.has("theme")) ThemeManager.set(root.get("theme").getAsString());

            JsonObject mods = obj(root, "modules");
            if (mods != null) {
                for (Module m : modules.all()) {
                    JsonObject o = obj(mods, m.getName());
                    if (o == null) continue;
                    if (o.has("enabled")) m.setEnabledQuiet(o.get("enabled").getAsBoolean());
                    if (o.has("key")) m.setKey(o.get("key").getAsInt());
                    JsonObject settings = obj(o, "settings");
                    if (settings == null) continue;
                    for (Setting<?> s : m.getSettings()) {
                        JsonElement e = settings.get(s.getName());
                        if (e != null) s.deserialize(e);
                    }
                }
            }

            JsonObject hudObj = obj(root, "hud");
            if (hudObj != null) {
                for (HudWidget w : hud.widgets()) {
                    JsonObject o = obj(hudObj, w.getName());
                    if (o == null) continue;
                    w.setPosition(o.get("x").getAsInt(), o.get("y").getAsInt());
                    w.setEnabled(o.get("enabled").getAsBoolean());
                }
            }

            JsonObject gui = obj(root, "gui");
            if (gui != null) {
                for (Category c : Category.values()) {
                    JsonObject o = obj(gui, c.name());
                    if (o == null) continue;
                    WindowState st = WindowState.of(c);
                    st.x = o.get("x").getAsInt();
                    st.y = o.get("y").getAsInt();
                    st.open = o.get("open").getAsBoolean();
                }
            }
        } catch (IOException | RuntimeException e) {
            ShadowClient.LOGGER.error("Failed to load config (using defaults)", e);
        }
    }

    private static JsonObject obj(JsonObject parent, String key) {
        JsonElement e = parent.get(key);
        return e != null && e.isJsonObject() ? e.getAsJsonObject() : null;
    }
}
