package me.night0721.lilase.utils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class BlockUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static int getUnitX() {
        double modYaw = (mc.thePlayer.rotationYaw % 360 + 360) % 360;
        if (modYaw < 45 || modYaw > 315) {
            return 0;
        } else if (modYaw < 135) {
            return -1;
        } else if (modYaw < 225) {
            return 0;
        } else {
            return 1;
        }
    }

    public static int getUnitZ() {
        double modYaw = (mc.thePlayer.rotationYaw % 360 + 360) % 360;
        if (modYaw < 45 || modYaw > 315) {
            return 1;
        } else if (modYaw < 135) {
            return 0;
        } else if (modYaw < 225) {
            return -1;
        } else {
            return 0;
        }
    }

    public static Block getRelativeBlock(float x, float y, float z) {
        return (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + (getUnitX() * z) + (getUnitZ() * -1 * x), mc.thePlayer.posY + y, mc.thePlayer.posZ + (getUnitZ() * z) + (getUnitX() * x))).getBlock());
    }
}