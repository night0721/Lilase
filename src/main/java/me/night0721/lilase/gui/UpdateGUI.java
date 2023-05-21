package me.night0721.lilase.gui;

import com.google.gson.JsonParser;
import me.night0721.lilase.Lilase;
import me.night0721.lilase.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
   Credits: FarmHelper
 */
public class UpdateGUI extends GuiScreen {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static boolean shownGui = false;
    public static boolean outdated = false;
    private static String[] message;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(0);
        float scale = 3;
        GL11.glScalef(scale, scale, 0.0F);
        this.drawCenteredString(mc.fontRendererObj, "Outdated version of Lilase", (int) (this.width / 2f / scale), (int) (this.height / 6f / scale), Color.RED.darker().getRGB());
        GL11.glScalef(1.0F / scale, 1.0F / scale, 0.0F);
        scale = 1.5f;
        GL11.glScalef(scale, scale, 0.0F);
        this.drawString(mc.fontRendererObj, "What's new? ➤", (int) (this.width / 2f / scale - 180), (int) (this.height / 6 / scale + 25), Color.GREEN.getRGB());
        GL11.glScalef(1.0F / scale, 1.0F / scale, 0.0F);
        if (message != null) {
            int y = 40;
            for (String s : message) {
                this.drawString(mc.fontRendererObj, s, this.width / 2 - 160, this.height / 6 + y, Color.WHITE.getRGB());
                y += 15;
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();
        registerButtons();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(new GuiMainMenu());
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)  {
        switch (button.id) {
            case 1: // closebtn
                mc.displayGuiScreen(new GuiMainMenu());
                break;
            case 2: // downloadbtn
                Utils.openURL("https://github.com/night0721/Lilase/releases/latest");
                mc.displayGuiScreen(new GuiMainMenu());
                break;
        }
    }

    private void registerButtons() {
        GuiButton closeBtn = new GuiButton(1, this.width / 2, this.height / 2 + 100, 150, 20, "Close");
        this.buttonList.add(closeBtn);

        GuiButton downloadBtn = new GuiButton(2, this.width / 2 - 150, this.height / 2 + 100, 150, 20, "Download new version");
        this.buttonList.add(downloadBtn);
    }

    public static void showGUI() {
        if (!shownGui && isOutdated()) {
            mc.displayGuiScreen(new UpdateGUI());
            shownGui = true;
            outdated = isOutdated();
            message = getReleaseMessage().replaceAll("\r", "").replace("+ ", "§a+ ").replace("= ", "§f= ").replace("- ", "§c- ").split("\n");
        }
    }
    private static boolean isOutdated() {
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL("https://api.github.com/repos/night0721/Lilase/releases/latest");
            HttpURLConnection conn = ((HttpURLConnection)url.openConnection());
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
            conn.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
            }
            String latestversion = new JsonParser().parse(result.toString()).getAsJsonObject().get("tag_name").getAsString().replace("v", "");
            return !Lilase.VERSION.contains(latestversion);
        } catch (Exception e) {
            return false;
        }
    }

    private static String getReleaseMessage() {
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL("https://api.github.com/repos/night0721/Lilase/releases/latest");
            HttpURLConnection conn = ((HttpURLConnection)url.openConnection());
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
            conn.setRequestMethod("GET");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
            }
            return new JsonParser().parse(result.toString()).getAsJsonObject().get("body").getAsString();

        } catch (Exception e) {
            return "No release message was found.";
        }
    }

}