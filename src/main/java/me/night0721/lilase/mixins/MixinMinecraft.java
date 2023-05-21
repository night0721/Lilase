package me.night0721.lilase.mixins;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.night0721.lilase.Lilase.VERSION;

@Mixin({Minecraft.class})
public class MixinMinecraft {
    @Inject(method = {"startGame"}, at = {@At("RETURN")})
    private void startGame(CallbackInfo ci) {
        Display.setTitle("Lilase v" + VERSION);
    }
}