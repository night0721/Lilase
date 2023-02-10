package me.night0721.lilase.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

import java.util.ArrayList;

public class PlayerUtils {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static final ArrayList<Packet<?>> packets = new ArrayList<>();

    public static void sendPacketWithoutEvent(Packet<?> packet) {
        packets.add(packet);
        mc.getNetHandler().getNetworkManager().sendPacket(packet);

    }
}
