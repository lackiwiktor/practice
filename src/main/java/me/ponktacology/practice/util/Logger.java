package me.ponktacology.practice.util;

import me.ponktacology.practice.Practice;

public class Logger {

  public static void log(String message) {
    System.out.println(message);
  }

  public static void log(String message, Object... args) {
    if (args == null) {
      log(message);
      return;
    }

    log(String.format(message, args));
  }

  public static void debug(String message, Object... args) {
    if (!Practice.getPractice().isDebug()) {
      return;
    }

    log("[DEBUG] ".concat(message), args);
  }
}
