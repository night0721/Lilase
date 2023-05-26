package me.night0721.lilase.events;

import lombok.Getter;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

// TODO: Implement Lombok Library for clean code
@Cancelable
public class PacketSentEvent extends Event {
    private @Getter final Packet<?> packet;

    public PacketSentEvent(Packet<?> packet) {
        this.packet = packet;
    }
}
