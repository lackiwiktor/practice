package me.ponktacology.practice.match.info;

import me.ponktacology.practice.player.PracticePlayer;
import lombok.Data;

@Data
public class PlayerMatchInfo {
    private boolean dead;
    private boolean disconnected;
    private PracticePlayer lastAttacker;
}
