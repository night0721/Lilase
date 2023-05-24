package me.night0721.lilase.remotecontrol;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.remotecontrol.events.CommandListener;
import me.night0721.lilase.remotecontrol.events.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class RemoteControl {
    public JDA bot;

    public RemoteControl() {
        init();
    }

    public void init() {
        boolean remoteControl = Lilase.configHandler.getBoolean("RemoteControl");
        String token = Lilase.configHandler.getString("BotToken");
        if (!remoteControl) {
            System.out.println("[Lilase Remote Control] Remote control disabled, skipping");
            return;
        }
        if (token == null || token.equals("")) {
            System.out.println("[Lilase Remote Control] Bot token not set, disabling remote control");
            return;
        }
        System.out.println("[Lilase Remote Control] Enabling remote control bot");
        JDABuilder jda = JDABuilder.createDefault(token);
        jda.addEventListeners(new ReadyListener(), new CommandListener());
        jda.setActivity(Activity.watching("your COFL macro"));
        jda.setStatus(OnlineStatus.ONLINE);
        bot = jda.build();

        bot.updateCommands().addCommands(
                Commands.slash("statistics", "Statistics of current session").setGuildOnly(true),
                Commands.slash("screenshot", "Take a screenshot of the client"),
                Commands.slash("enable", "Enable a feature of the mod")
                        .setGuildOnly(true)
                        .addOptions(new OptionData(OptionType.STRING, "type", "The type of feature to turn on")
                                .addChoice("Auto Claimer", "claimer")
                                .addChoice("COFL Macro", "macro")
                                .addChoice("Auto Relister", "relister"))
        ).queue();
    }
}
