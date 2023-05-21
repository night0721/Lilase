package me.night0721.lilase.utils;

import me.night0721.lilase.Lilase;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InventoryUtils { public static boolean isToAuctionItem(String uuid) {
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

    public static void openInventory() {
        Lilase.mc.displayGuiScreen(new GuiInventory(Lilase.mc.thePlayer));
    }

    public static ItemStack getStackInSlot(final int slot) {
        return Lilase.mc.thePlayer.inventory.getStackInSlot(slot);
    }

    public static ItemStack getStackInOpenContainerSlot(final int slot) {
        if (Lilase.mc.thePlayer.openContainer.inventorySlots.get(slot).getHasStack())
            return Lilase.mc.thePlayer.openContainer.inventorySlots.get(slot).getStack();
        return null;
    }

    public static int getSlotForItemm(String id) {
        for (final Slot slot : Lilase.mc.thePlayer.inventoryContainer.inventorySlots) {
            if (slot.getHasStack()) {
                final ItemStack is = slot.getStack();
                if (is.getTagCompound().getCompoundTag("ExtraAttributes").getString("uuid").equals(id)) return slot.getSlotIndex();
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


    public static int getAvailableHotbarSlot(final String name) {
        for (int i = 0; i < 8; ++i) {
            final ItemStack is = Lilase.mc.thePlayer.inventory.getStackInSlot(i);
            if (is == null || is.getDisplayName().contains(name)) {
                return i;
            }
        }
        return -1;
    }

    public static List<Integer> getAllSlots(final String name) {
        final List<Integer> ret = new ArrayList<>();
        for (int i = 9; i < 44; ++i) {
            final ItemStack is = Lilase.mc.thePlayer.inventoryContainer.inventorySlots.get(i).getStack();
            if (is != null && is.getDisplayName().contains(name)) {
                ret.add(i);
            }
        }
        return ret;
    }

    public static int getAmountInHotbar(final String item) {
        for (int i = 0; i < 8; ++i) {
            final ItemStack is = Lilase.mc.thePlayer.inventory.getStackInSlot(i);
            if (is != null && StringUtils.stripControlCodes(is.getDisplayName()).equals(item)) {
                return is.stackSize;
            }
        }
        return 0;
    }

    public static int getItemInHotbar(final String itemName) {
        for (int i = 0; i < 8; ++i) {
            final ItemStack is = Lilase.mc.thePlayer.inventory.getStackInSlot(i);
            if (is != null && StringUtils.stripControlCodes(is.getDisplayName()).contains(itemName)) {
                return i;
            }
        }
        return -1;
    }

    public static List<ItemStack> getInventoryStacks() {
        final List<ItemStack> ret = new ArrayList<>();
        for (int i = 9; i < 44; ++i) {
            final Slot slot = Lilase.mc.thePlayer.inventoryContainer.getSlot(i);
            if (slot != null) {
                final ItemStack stack = slot.getStack();
                if (stack != null) {
                    ret.add(stack);
                }
            }
        }
        return ret;
    }

    public static List<Slot> getInventorySlots() {
        final List<Slot> ret = new ArrayList<>();
        for (int i = 9; i < 44; ++i) {
            final Slot slot = Lilase.mc.thePlayer.inventoryContainer.getSlot(i);
            if (slot != null) {
                final ItemStack stack = slot.getStack();
                if (stack != null) {
                    ret.add(slot);
                }
            }
        }
        return ret;
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