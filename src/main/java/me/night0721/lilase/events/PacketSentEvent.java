package me.night0721.lilase.events;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PacketSentEvent extends Event {
    public final Packet<?> packet;

    public PacketSentEvent(Packet<?> packet) {
        this.packet = packet;
    }
}
