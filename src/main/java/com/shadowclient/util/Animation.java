package com.shadowclient.util;

/** Frame-rate independent exponential smoothing toward a target value. */
public final class Animation {
    private float value;
    private float target;
    private final float speed;
    private long last = System.nanoTime();

    public Animation(float initial, float speed) {
        this.value = initial;
        this.target = initial;
        this.speed = speed;
    }

    public void set(float target) { this.target = target; }

    public void snap(float v) { this.value = v; this.target = v; }

    public float target() { return target; }

    public float get() {
        long now = System.nanoTime();
        float dt = Math.min((now - last) / 1_000_000_000f, 0.1f);
        last = now;
        value += (target - value) * (1f - (float) Math.exp(-speed * dt));
        if (Math.abs(target - value) < 0.002f) value = target;
        return value;
    }
}
