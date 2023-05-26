package me.night0721.lilase.utils;

import me.night0721.lilase.Lilase;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InventoryUtils {
    public static boolean isToAuctionItem(String uuid) {
        ItemStack stack = Lilase.mc.thePlayer.openContainer.getSlot(13).getStack();
        if (stack != null && stack.hasTagCompound()) {
            return stack.getTagCompound().getCompoundTag("ExtraAttributes").getString("uuid").equals(uuid);
        }
        return false;
    }

    public static boolean isStoneButton() {
        final ItemStack stack = Lilase.mc.thePlayer.openContainer.getSlot(13).getStack();
        if (stack != null && stack.hasTagCompound()) {
            final NBTTagCompound tag = stack.getTagCompound();
            final Pattern pattern = Pattern.compile("Click an item in your inventory!", Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(tag.toString());
            while (matcher.find()) {
                if (matcher.group(0) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getInventoryName() {
        if (Lilase.mc.currentScreen instanceof GuiChest) {
            final ContainerChest chest = (ContainerChest) Lilase.mc.thePlayer.openContainer;
            final IInventory inv = chest.getLowerChestInventory();
            return inv.hasCustomName() ? inv.getName() : null;
        }
        return null;
    }

    public static boolean inventoryNameStartsWith(String startsWithString) {
        return getInventoryName() != null && getInventoryName().startsWith(startsWithString);
    }

    public static boolean inventoryNameContains(String startsWithString) {
        return getInventoryName() != null && getInventoryName().contains(startsWithString);
    }

    public static ItemStack getStackInOpenContainerSlot(final int slot) {
        if (Lilase.mc.thePlayer.openContainer.inventorySlots.get(slot).getHasStack())
            return Lilase.mc.thePlayer.openContainer.inventorySlots.get(slot).getStack();
        return null;
    }

    public static int getSlotForItem(String id) {
        for (final Slot slot : Lilase.mc.thePlayer.inventoryContainer.inventorySlots) {
            if (slot.getHasStack()) {
                final ItemStack is = slot.getStack();
                if (is.getTagCompound().getCompoundTag("ExtraAttributes").getString("uuid").equals(id))
                    return slot.getSlotIndex();
            }
        }
        return -1;
    }

    public static void clickOpenContainerSlot(final int slot, final int button, final int clickType) {
        Lilase.mc.playerController.windowClick(Lilase.mc.thePlayer.openContainer.windowId, slot, button, clickType, Lilase.mc.thePlayer);
    }

    public static void clickOpenContainerSlot(final int slot) {
        clickOpenContainerSlot(slot, 0, 0);
    }

    public static void clickWindow(int window, int slot) {
        Lilase.mc.playerController.windowClick(window, slot, 0, 0, Lilase.mc.thePlayer);
    }

    public static NBTTagList getLore(ItemStack item) {
        if (item == null) {
            throw new NullPointerException("The item cannot be null!");
        }
        if (!item.hasTagCompound()) {
            return null;
        }

        return item.getSubCompound("display", false).getTagList("Lore", 8);
    }
}