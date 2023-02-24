package me.night0721.lilase.events;

import io.netty.channel.ChannelHandlerContext;
import me.night0721.lilase.features.flip.Flipper;
import me.night0721.lilase.utils.Utils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.io.IOException;

import static me.night0721.lilase.features.ah.AuctionHouse.flipper;
import static me.night0721.lilase.features.flip.FlipperState.START;
import static me.night0721.lilase.utils.PlayerUtils.sendPacketWithoutEvent;

@Cancelable
public class PacketReceivedEvent extends Event {
    public final Packet<?> packet;
    public final ChannelHandlerContext context;

    public PacketReceivedEvent(final Packet<?> packet, final ChannelHandlerContext context) {
        this.packet = packet;
        this.context = context;
        if (packet instanceof S33PacketUpdateSign && Utils.checkInHub() && Flipper.state.equals(START)) {
            new Thread(() -> {
                try {
                    S33PacketUpdateSign packetUpdateSign = (S33PacketUpdateSign) packet;
                    IChatComponent[] lines = packetUpdateSign.getLines();
                    Utils.debugLog("[Flipper] Item price should be " + flipper.getItemPrice());
                    lines[0] = IChatComponent.Serializer.jsonToComponent("{\"text\":\"" + flipper.getItemPrice() + "\"}");
                    Thread.sleep(1500);
                    C12PacketUpdateSign packetUpdateSign1 = new C12PacketUpdateSign(packetUpdateSign.getPos(), lines);
                    sendPacketWithoutEvent(packetUpdateSign1);
                } catch (IOException | InterruptedException | RuntimeException e) {
                    e.printStackTrace();
                }

            }).start();
        }
    }
}
