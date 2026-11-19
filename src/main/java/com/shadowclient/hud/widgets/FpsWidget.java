package com.shadowclient.hud.widgets;

public final class FpsWidget extends LabelWidget {
    public FpsWidget(int x, int y) { super("FPS", x, y); }

    @Override protected String label() { return "FPS"; }

    @Override protected String value(boolean editing) { return String.valueOf(mc.getCurrentFps()); }
}
