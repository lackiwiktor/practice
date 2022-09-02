package me.ponktacology.practice.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;

@UtilityClass
public class LocationUtil {

    public static boolean hasChanged(Location from, Location to) {
        return hasChanged(from, to, false);
    }

    public static boolean hasChanged(Location from, Location to, boolean ignoreY) {
        return !from.getWorld().equals(to.getWorld()) ||
                from.getBlockX() != to.getBlockX() ||
                (!ignoreY && from.getBlockY() != to.getBlockY()) ||
                from.getBlockZ() != to.getBlockZ();
    }
}
