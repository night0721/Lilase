package me.night0721.lilase.utils;

import me.night0721.lilase.Lilase;
import net.minecraft.network.Packet;

import java.util.ArrayList;

public class PlayerUtils {
    public static final ArrayList<Packet<?>> packets = new ArrayList<>();

    public static void sendPacketWithoutEvent(Packet<?> packet) {
        packets.add(packet);
        Lilase.mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }
}
