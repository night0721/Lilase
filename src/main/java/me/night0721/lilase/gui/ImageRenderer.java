package me.night0721.lilase.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static me.night0721.lilase.Lilase.mc;

public class ImageRenderer {
    public void draw() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        GlStateManager.pushMatrix();
        mc.getTextureManager().bindTexture(new ResourceLocation("lilase:images/crabby.png"));
//        GlStateManager.color(CurrentColor.getFloatValue(0, 0), CurrentColor.getFloatValue(0, 1), CurrentColor.getFloatValue(0, 2));
        Gui.drawModalRectWithCustomSizedTexture(scaledResolution.getScaledWidth() / 2 + 64, scaledResolution.getScaledHeight() / 2 + 64, 0, 0, 128, 32, 128, 32);
        GlStateManager.popMatrix();
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent e) {
        if (e.type == RenderGameOverlayEvent.ElementType.ALL) {
            draw();
        }
    }
}
