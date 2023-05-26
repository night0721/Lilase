package me.night0721.lilase.utils.capes;

import me.night0721.lilase.Lilase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
/**
 * Credits: Gabagooooooooooool
 */
public class CapeManager {
    @SubscribeEvent
    public void onJoin(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayer) {
            Lilase.mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.CAPE, true);
            Lilase.mc.getRenderManager().getSkinMap().values().forEach(p -> p.addLayer(new CapeLayer(p)));
        }
    }
}