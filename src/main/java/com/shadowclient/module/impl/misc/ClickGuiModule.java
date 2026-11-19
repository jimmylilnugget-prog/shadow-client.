package com.shadowclient.module.impl.misc;

import com.shadowclient.gui.ClickGuiScreen;
import com.shadowclient.module.Category;
import com.shadowclient.module.Module;
import org.lwjgl.glfw.GLFW;

/** Action module: its keybind (default Right Shift) opens the ClickGUI. */
public final class ClickGuiModule extends Module {
    public ClickGuiModule() {
        super("ClickGUI", "Opens the Shadow ClickGUI.", Category.MISC);
        setKey(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    @Override
    public void toggle() {
        if (!(mc.currentScreen instanceof ClickGuiScreen)) mc.setScreen(new ClickGuiScreen());
    }
}
