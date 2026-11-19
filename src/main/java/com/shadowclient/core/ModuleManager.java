package com.shadowclient.core;

import com.shadowclient.ShadowClient;
import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import com.shadowclient.module.impl.combat.AutoTotem;
import com.shadowclient.module.impl.combat.KillAura;
import com.shadowclient.module.impl.combat.TriggerBot;
import com.shadowclient.module.impl.misc.AntiAFK;
import com.shadowclient.module.impl.misc.ClickGuiModule;
import com.shadowclient.module.impl.misc.Notifications;
import com.shadowclient.module.impl.misc.Panic;
import com.shadowclient.module.impl.movement.AutoWalk;
import com.shadowclient.module.impl.movement.Fly;
import com.shadowclient.module.impl.movement.Speed;
import com.shadowclient.module.impl.movement.Sprint;
import com.shadowclient.module.impl.movement.Step;
import com.shadowclient.module.impl.player.AutoRespawn;
import com.shadowclient.module.impl.player.AutoTool;
import com.shadowclient.module.impl.player.Sneak;
import com.shadowclient.module.impl.render.ESP;
import com.shadowclient.module.impl.render.Fullbright;
import com.shadowclient.module.impl.render.HudModule;
import com.shadowclient.module.impl.world.AutoMine;
import com.shadowclient.module.impl.world.AutoUse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ModuleManager {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final List<Module> modules = new ArrayList<>();
    private final Map<Module, Boolean> keyState = new HashMap<>();
    private final Set<Module> crashed = new HashSet<>();

    public ModuleManager() {
        // Combat
        register(new KillAura());
        register(new TriggerBot());
        register(new AutoTotem());
        // Movement
        register(new Sprint());
        register(new Fly());
        register(new Speed());
        register(new Step());
        register(new AutoWalk());
        // Player
        register(new AutoRespawn());
        register(new AutoTool());
        register(new Sneak());
        // Render
        register(new Fullbright());
        register(new ESP());
        register(new HudModule());
        // World
        register(new AutoMine());
        register(new AutoUse());
        // Misc
        register(new ClickGuiModule());
        register(new Panic());
        register(new AntiAFK());
        register(new Notifications());
    }

    private void register(Module m) { modules.add(m); }

    public List<Module> all() { return Collections.unmodifiableList(modules); }

    public List<Module> inCategory(Category c) {
        List<Module> out = new ArrayList<>();
        for (Module m : modules) if (m.getCategory() == c) out.add(m);
        return out;
    }

    public <T extends Module> T get(Class<T> type) {
        for (Module m : modules) if (type.isInstance(m)) return type.cast(m);
        return null;
    }

    public Module byName(String name) {
        for (Module m : modules) if (m.getName().equalsIgnoreCase(name)) return m;
        return null;
    }

    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        pollKeybinds();
        for (Module m : modules) {
            if (!m.isEnabled()) continue;
            try {
                m.onTick();
            } catch (Throwable t) {
                if (crashed.add(m)) ShadowClient.LOGGER.error("Module {} crashed and was disabled", m.getName(), t);
                m.setEnabledQuiet(false);
            }
        }
    }

    /** Polls GLFW directly so keybinds are fully customisable without touching vanilla controls. */
    private void pollKeybinds() {
        long handle = mc.getWindow().getHandle();
        boolean canFire = mc.currentScreen == null;
        for (Module m : modules) {
            int key = m.getKey();
            if (key <= 0) continue;
            boolean down = GLFW.glfwGetKey(handle, key) == GLFW.GLFW_PRESS;
            boolean was = keyState.getOrDefault(m, false);
            if (down && !was && canFire) m.toggle();
            keyState.put(m, down);
        }
    }

    public void notifyToggle(Module m) {
        Notifications n = get(Notifications.class);
        if (n == null || !n.isEnabled() || m == n || mc.player == null) return;
        String state = m.isEnabled() ? "\u00A7aEnabled" : "\u00A7cDisabled";
        mc.player.sendMessage(Text.literal("\u00A77" + m.getName() + " " + state), true);
    }
}
