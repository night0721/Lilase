package me.night0721.lilase.mixins;

import io.netty.channel.ChannelHandlerContext;
import me.night0721.lilase.events.PacketReceivedEvent;
import me.night0721.lilase.events.PacketSentEvent;
import me.night0721.lilase.utils.PlayerUtils;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {NetworkManager.class})
public class MixinNetworkManager {
    @Inject(method = {"channelRead0*"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void read(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new PacketReceivedEvent(packet, context))) {
            ci.cancel();
        }
    }

    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (!PlayerUtils.packets.contains(packet)) {
            if (MinecraftForge.EVENT_BUS.post(new PacketSentEvent(packet))) {
                ci.cancel();
            }
        } else {
            PlayerUtils.packets.remove(packet);
        }
    }
}
