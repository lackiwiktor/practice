package me.ponktacology.practice.ladder;

import lombok.Getter;

@Getter
public enum LadderType {
    NORMAL(false, true),
    SUMO(false, false),
    BOXING(false, false),
    BUILD_UHC(true, true),
    COMBO(false, false);

    private final boolean build;
    private final boolean hungerDecay;

    LadderType(boolean build, boolean hungerDecay) {
        this.build = build;
        this.hungerDecay = hungerDecay;
    }
}
