package me.night0721.lilase.remotecontrol.events;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public class ReadyListener implements EventListener {
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof ReadyEvent){
            ReadyEvent readyEvent = (ReadyEvent) event;
            System.out.println("[Lilase Remote Control] " + readyEvent.getJDA().getSelfUser().getName() + " is ready!");
        }
    }
}