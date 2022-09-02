package me.ponktacology.practice.util;

import lombok.Data;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

@Data
public class Selection {

    private @Nullable Location first;
    private @Nullable Location second;

    public boolean isReady() {
        return first != null && second != null;
    }
}
