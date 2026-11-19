package com.shadowclient.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public final class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, boolean value) { super(name, value); }

    public void toggle() { value = !value; }

    @Override public JsonElement serialize() { return new JsonPrimitive(value); }

    @Override public void deserialize(JsonElement e) { value = e.getAsBoolean(); }
}
