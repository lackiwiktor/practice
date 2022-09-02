package me.ponktacology.practice.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

@UtilityClass
public class EventUtil {

  public static void callEvent(Event event) {
    Bukkit.getPluginManager().callEvent(event);
  }
}
