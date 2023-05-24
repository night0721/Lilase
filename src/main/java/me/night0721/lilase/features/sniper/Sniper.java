package me.night0721.lilase.features.sniper;

import lombok.*;
import me.night0721.lilase.utils.Clock;

public abstract class Sniper {
    public final Clock cooldown = new Clock();
    public @Getter @Setter boolean open = false;

    public abstract void toggle();


    public abstract void onTick();
}
