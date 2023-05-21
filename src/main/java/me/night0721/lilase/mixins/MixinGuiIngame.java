package me.night0721.lilase.mixins;

import me.night0721.lilase.events.ScoreboardRenderEvent;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Pattern;

/*
 Credits: Cephetir
 */
@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @ModifyArg(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I"))
    public String a(String text) {
        String txt = keepScoreboardCharacters(stripColor(text)).trim();
        if (txt.startsWith("www")) return "  §4w§cw§6w§e.§2n§ai§bg§3h§1t§90§d7§42§c1§6.§em§2e  ";
        if (txt.startsWith("SKY")) return "   §d§lLILASE   ";
        if (Pattern.compile("\\d{2}/\\d{2}/\\d{2}").matcher(txt).find()) return txt.split(" ")[0];
        if (text.startsWith(String.valueOf(EnumChatFormatting.RED)) && Pattern.compile("\\d+").matcher(txt).matches()) return "";
        else return text;
    }

    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    public void renderScoreboard(ScoreObjective s, ScaledResolution score, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new ScoreboardRenderEvent(s, score))) ci.cancel();
    }

    public String keepScoreboardCharacters(String str) {
        return str.replaceAll("[^a-z A-Z:\\d/'.]", "");
    }

    public String stripColor(String str) {
        return str.replaceAll("(?i)§[\\dA-FK-OR]", "");
    }
}