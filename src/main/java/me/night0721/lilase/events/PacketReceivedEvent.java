package me.night0721.lilase.events;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

// TODO: Implement Lombok Library for clean code
@Cancelable
public class PacketReceivedEvent extends Event {
    public final Packet<?> packet;
    public final ChannelHandlerContext context;

    public PacketReceivedEvent(final Packet<?> packet, final ChannelHandlerContext context) {
        this.packet = packet;
        this.context = context;
    }
}
