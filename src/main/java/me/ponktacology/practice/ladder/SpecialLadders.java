package me.ponktacology.practice.ladder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SpecialLadders {
  SUMO("Sumo"),
  BOXING("Boxing"),
  BRIDGE("Bridge"),
  EGGS_HOT("EggShot"),

  COMBO("Combo");

  public static boolean isSpecial(Ladder ladder, SpecialLadders type) {
    return type.name.equals(ladder.getName());
  }

  private final String name;
}
