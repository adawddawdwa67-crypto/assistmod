package com.example.assistmod.modules;

import net.minecraft.client.MinecraftClient;

public abstract class Module {
    public final String name;
    private boolean enabled;

    protected Module(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        enabled = !enabled;
    }

    /** Вызывается каждый клиентский тик, только когда модуль включён. */
    public abstract void onTick(MinecraftClient mc);
}
