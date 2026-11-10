package com.example.assistmod;

import com.example.assistmod.modules.AimAssist;
import com.example.assistmod.modules.Module;
import com.example.assistmod.modules.TriggerBot;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class AssistMod implements ClientModInitializer {
    private static final String CATEGORY = "key.categories.assistmod";

    private final TriggerBot triggerBot = new TriggerBot();
    private final AimAssist aimAssist = new AimAssist();
    private final List<Module> modules = List.of(aimAssist, triggerBot);

    private KeyBinding triggerKey;
    private KeyBinding aimKey;

    @Override
    public void onInitializeClient() {
        triggerKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.assistmod.trigger", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY));
        aimKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.assistmod.aim", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(MinecraftClient mc) {
        while (triggerKey.wasPressed()) toggle(mc, triggerBot);
        while (aimKey.wasPressed()) toggle(mc, aimAssist);

        if (mc.player == null) return;
        for (Module m : modules) {
            if (m.isEnabled()) m.onTick(mc);
        }
    }

    private void toggle(MinecraftClient mc, Module m) {
        m.toggle();
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal(m.name + ": " + (m.isEnabled() ? "ON" : "OFF")), true);
        }
    }
}
