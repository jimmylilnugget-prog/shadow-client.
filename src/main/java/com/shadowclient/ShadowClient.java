package com.shadowclient;

import com.shadowclient.config.ConfigManager;
import com.shadowclient.core.CpsTracker;
import com.shadowclient.core.ModuleManager;
import com.shadowclient.core.TargetTracker;
import com.shadowclient.hud.HudManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Client entrypoint: wires the managers together and hooks into Fabric events. */
public final class ShadowClient implements ClientModInitializer {
    public static final String ID = "shadowclient";
    public static final String NAME = "Shadow Client";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    private static ModuleManager modules;
    private static HudManager hud;
    private static ConfigManager config;

    public static ModuleManager modules() { return modules; }
    public static HudManager hud() { return hud; }
    public static ConfigManager config() { return config; }

    @Override
    public void onInitializeClient() {
        modules = new ModuleManager();
        hud = new HudManager();
        config = new ConfigManager(modules, hud);
        config.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            CpsTracker.update();
            TargetTracker.update();
            modules.onTick();
        });

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.MISC_OVERLAYS,
                Identifier.of(ID, "hud"),
                (context, tickCounter) -> hud.render(context));

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> config.save());

        LOGGER.info("{} v{} initialised with {} modules", NAME, VERSION, modules.all().size());
    }
}
