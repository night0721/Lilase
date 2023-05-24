package me.night0721.lilase.gui;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.config.AHConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Calendar;
import java.util.TimeZone;

import static me.night0721.lilase.Lilase.mc;
import static me.night0721.lilase.config.AHConfig.GUI_COLOR;

public class GUIRenderer {
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post e) {
        if (e.type == RenderGameOverlayEvent.ElementType.ALL && AHConfig.CRABBY) {
            mc.getTextureManager().bindTexture(new ResourceLocation("lilase:textures/images/crab.png"));
            Gui.drawModalRectWithCustomSizedTexture(100, 0, 0, 0, 100, 100, 100, 100);
        }
    }

    @SubscribeEvent
    public void onGuiRender(RenderGameOverlayEvent event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            if (AHConfig.GUI) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeZone(TimeZone.getDefault());
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                String time = String.format("%02d:%02d", hour, minute);
                int days = (int) (Lilase.mc.theWorld.getWorldTime() / 24000);
                String lines = "X: " + Math.round(Lilase.mc.thePlayer.posX) + "\n" + "Y: " + Math.round(Lilase.mc.thePlayer.posY) + "\n" + "Z: " + Math.round(Lilase.mc.thePlayer.posZ) + "\n" + time + "\n" + "FPS: " + Minecraft.getDebugFPS() + "\n" + "Day: " + days + "\n" + "Auctions Sniped: " + Lilase.cofl.bought_items.size() + "\n" + "Auctions Flipped: " + Lilase.cofl.sold_items.size() + "\n";
                TextRenderer.drawString(lines, 0, 0, 0.9, GUI_COLOR.getRGB());
            }
        } else if (event.type == RenderGameOverlayEvent.ElementType.CHAT) {
            if (AHConfig.HKNO1) {
                TextRenderer.drawGradientString(Lilase.mc.fontRendererObj, "Lilase", 50, 100, 0x00FBAA, 0xFF3EFC);
                TextRenderer.drawAnimatedString(Lilase.mc.fontRendererObj, "Hong Kong No.1", 50, 110, 0x00FBAA, 0xFF3EFC, 0.5f);
            }
        }
    }
}
