package me.night0721.lilase.remotecontrol.events;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.remotecontrol.BotUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("statistics")) {
            event.deferReply().queue();
            ArrayList<HashMap<String, String>> sold_items = Lilase.cofl.sold_items;
            ArrayList<HashMap<String, String>> bought_items = Lilase.cofl.bought_items;
            EmbedBuilder embed = new EmbedBuilder();
            embed.setAuthor("Lilase", "https://camo.githubusercontent.com/57a8295f890970d2173b895c7a0f6c60527fb3bec4489b233b221ab45cb9fa42/68747470733a2f2f63646e2e646973636f72646170702e636f6d2f6174746163686d656e74732f3834323031343930393236343935333335342f313038323337333237353033383030333231302f6c696c6173652e706e67");
            embed.setTitle("Current Session Statistics");
            embed.addField("Auctions sold", String.valueOf(sold_items.size()), true);
            embed.addField("Auctions bought", String.valueOf(bought_items.size()), true);
            embed.setTimestamp(event.getTimeCreated());
            embed.setImage("attachment://image.png");
            embed.setFooter("Made by night0721", "https://avatars.githubusercontent.com/u/77528305?v=4");
            FileUpload screenshot = FileUpload.fromData(new File(Objects.requireNonNull(BotUtils.takeScreenShot())), "image.png");
            event.getHook().editOriginalAttachments(screenshot).queue();
            event.getHook().editOriginalEmbeds(embed.build()).queue();

            if (sold_items.size() > 0) {
                EmbedBuilder sold = new EmbedBuilder();
                sold.setAuthor("Lilase", "https://camo.githubusercontent.com/57a8295f890970d2173b895c7a0f6c60527fb3bec4489b233b221ab45cb9fa42/68747470733a2f2f63646e2e646973636f72646170702e636f6d2f6174746163686d656e74732f3834323031343930393236343935333335342f313038323337333237353033383030333231302f6c696c6173652e706e67");
                sold.setTitle("Sold Items");
                sold.setFooter("Made by night0721", "https://avatars.githubusercontent.com/u/77528305?v=4");
                sold.setTimestamp(event.getTimeCreated());
                for (HashMap<String, String> sold_item : sold_items) {
                    sold.addField(sold_item.get("item"), "Price: " + sold_item.get("price"), true);
                }
                event.getHook().getInteraction().getMessageChannel().sendMessageEmbeds(sold.build()).queue();
            }
            if (bought_items.size() > 0) {
                EmbedBuilder bought = new EmbedBuilder();
                bought.setAuthor("Lilase", "https://camo.githubusercontent.com/57a8295f890970d2173b895c7a0f6c60527fb3bec4489b233b221ab45cb9fa42/68747470733a2f2f63646e2e646973636f72646170702e636f6d2f6174746163686d656e74732f3834323031343930393236343935333335342f313038323337333237353033383030333231302f6c696c6173652e706e67");
                bought.setTitle("Bought Items");
                bought.setFooter("Made by night0721", "https://avatars.githubusercontent.com/u/77528305?v=4");
                bought.setTimestamp(event.getTimeCreated());
                for (HashMap<String, String> bought_item : bought_items) {
                    bought.addField(bought_item.get("name"), "Price: " + bought_item.get("price"), true);
                }
                event.getHook().getInteraction().getMessageChannel().sendMessageEmbeds(bought.build()).queue();
            }

        }
        if (event.getName().equals("screenshot")) {
            event.deferReply().queue();
            FileUpload screenshot = FileUpload.fromData(new File(Objects.requireNonNull(BotUtils.takeScreenShot())), "image.png");
            event.getHook().editOriginalAttachments(screenshot).queue();
        }
        if (event.getName().equals("enable")) {
            String type = Objects.requireNonNull(event.getOption("type")).getAsString();
            if (type.equals("claimer")) {
                if (!Lilase.relister.isOpen()) {
                    Lilase.claimer.toggle();
                    event.reply("Claimer enabled").queue();
                } else {
                    event.getHook().editOriginal("Relister is currently running, please stop it first").queue();
                }
            }
            if (type.equals("macro")) {
                if (!Lilase.cofl.isOpen()) {
                    Lilase.cofl.toggleAuction();
                    event.reply("Macro enabled").queue();
                } else {
                    event.reply("Macro is currently running, please stop it first").queue();
                }
            }
            if (type.equals("relister")) {
                if (!Lilase.claimer.isOpen()) {
                    Lilase.relister.toggle();
                    event.reply("Relister enabled").queue();
                } else {
                    event.reply("Claimer is currently running, please stop it first").queue();
                }
            }
        }
    }
}