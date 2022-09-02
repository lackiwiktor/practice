package me.ponktacology.practice.util.message;

import net.kyori.adventure.audience.Audience;

public interface Recipient extends Audience {
    void receive(String message);
}
