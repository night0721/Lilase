package me.night0721.lilase.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static me.night0721.lilase.Lilase.mc;

public class ImageRenderer {
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post e) {
        if (e.type == RenderGameOverlayEvent.ElementType.ALL) {
            mc.getTextureManager().bindTexture(new ResourceLocation("lilase:textures/images/crab.png"));
            Gui.drawModalRectWithCustomSizedTexture(100, 0, 0, 0, 100, 100, 100, 100);
        }
    }
}
