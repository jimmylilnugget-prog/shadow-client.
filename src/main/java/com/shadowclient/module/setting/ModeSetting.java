package com.shadowclient.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.List;

public final class ModeSetting extends Setting<String> {
    private final List<String> modes;

    public ModeSetting(String name, String value, String... modes) {
        super(name, value);
        this.modes = List.of(modes);
    }

    public List<String> modes() { return modes; }

    public boolean is(String mode) { return value.equalsIgnoreCase(mode); }

    public void cycle(int dir) {
        int i = modes.indexOf(value);
        value = modes.get(Math.floorMod(i + dir, modes.size()));
    }

    @Override public JsonElement serialize() { return new JsonPrimitive(value); }

    @Override
    public void deserialize(JsonElement e) {
        String s = e.getAsString();
        if (modes.contains(s)) value = s;
    }
}
