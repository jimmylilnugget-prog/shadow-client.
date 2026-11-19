package com.shadowclient.module;

public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    RENDER("Render"),
    WORLD("World"),
    MISC("Misc");

    private final String display;

    Category(String display) { this.display = display; }

    public String display() { return display; }
}
