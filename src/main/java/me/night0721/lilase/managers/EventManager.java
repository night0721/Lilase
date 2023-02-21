package me.night0721.lilase.managers;

import me.night0721.lilase.features.ah.AuctionHouse;
import me.night0721.lilase.features.ah.States;
import me.night0721.lilase.utils.ConfigUtils;
import me.night0721.lilase.utils.PlayerUtils;
import me.night0721.lilase.utils.Utils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventManager {
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        if (!message.contains(":")) {
            if (message.equals("You didn't participate in this auction!")) {
                System.out.println("Failed to buy item, not fast enough. Closing the menu");
                PlayerUtils.mc.playerController.windowClick(PlayerUtils.mc.thePlayer.openContainer.windowId, 49, 0, 0, PlayerUtils.mc.thePlayer); // Close the window as could not buy
            } else if (message.equals("You don't have enough coins to afford this bid!")) {
                System.out.println("Failed to buy item, not enough money. Closing the menu");
                AuctionHouse.clickState = States.NONE;
                PlayerUtils.mc.playerController.windowClick(PlayerUtils.mc.thePlayer.openContainer.windowId, 49, 0, 0, PlayerUtils.mc.thePlayer); // Close the window as could not buy
            } else if (message.contains("Your new API key is")) {
                System.out.println("Detected new API key, saving it to config");
                Utils.sendMessage("Saved new API key to config");
                String apiKey = message.replace("Your new API key is ", "");
                ConfigUtils.writeStringConfig("main", "APIKey", apiKey);
            } else if (message.equals("Claiming BIN auction...")) {
                AuctionHouse.clickState = States.EXECUTE;
            } else if (message.equals("This BIN sale is still in its grace period!")) {
                AuctionHouse.clickState = States.CLICK;
            }
        }
    }
}
