package me.night0721.lilase.mixins;

import me.night0721.lilase.utils.Utils;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
    @Inject(method = "handlePlayerListHeaderFooter", at = @At("HEAD"))
    public void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter packetIn, CallbackInfo ci) {
        Utils.header = packetIn.getHeader().getFormattedText().length() == 0 ? null : packetIn.getHeader();
        Utils.footer = packetIn.getFooter().getFormattedText().length() == 0 ? null : packetIn.getFooter();
    }
}
