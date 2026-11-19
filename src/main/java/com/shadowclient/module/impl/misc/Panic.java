package com.shadowclient.module.impl.misc;

import com.shadowclient.ShadowClient;
import com.shadowclient.module.Category;
import com.shadowclient.module.Module;

/** Action module: instantly disables every other module. */
public final class Panic extends Module {
    public Panic() {
        super("Panic", "Disables every module at once.", Category.MISC);
    }

    @Override
    public void toggle() {
        for (Module m : ShadowClient.modules().all()) {
            if (m != this && m.isEnabled() && !(m instanceof Notifications)) m.setEnabled(false);
        }
    }
}
