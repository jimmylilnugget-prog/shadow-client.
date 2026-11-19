package com.shadowclient.module.impl.misc;

import com.shadowclient.module.Category;
import com.shadowclient.module.Module;

/** When enabled, module toggles are announced on the action bar. Logic lives in ModuleManager. */
public final class Notifications extends Module {
    public Notifications() {
        super("Notifications", "Shows an action-bar message when modules are toggled.", Category.MISC);
        setEnabledQuiet(true);
    }
}
