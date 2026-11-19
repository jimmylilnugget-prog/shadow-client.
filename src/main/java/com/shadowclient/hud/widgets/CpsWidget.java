package com.shadowclient.hud.widgets;

import com.shadowclient.core.CpsTracker;

public final class CpsWidget extends LabelWidget {
    public CpsWidget(int x, int y) { super("CPS", x, y); }

    @Override protected String label() { return "CPS"; }

    @Override
    protected String value(boolean editing) { return CpsTracker.left() + " | " + CpsTracker.right(); }
}
