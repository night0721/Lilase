package me.night0721.lilase.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static me.night0721.lilase.Lilase.mc;

public class ImageRenderer {
    public void draw() {
//        GlStateManager.color(CurrentColor.getFloatValue(0, 0), CurrentColor.getFloatValue(0, 1), CurrentColor.getFloatValue(0, 2));
        GL11.glPushMatrix();
        mc.getTextureManager().bindTexture(new ResourceLocation("lilase:lilase.png"));

//        mc.getTextureManager().bindTexture(new ResourceLocation("lilase:textures/images/crabby.png"));
        Gui.drawModalRectWithCustomSizedTexture(5, 5, 0, 0, 100, 100, 100, 100);
        GL11.glPopMatrix();
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post e) {
        if (e.type == RenderGameOverlayEvent.ElementType.ALL) {
            draw();
        }
    }
}
