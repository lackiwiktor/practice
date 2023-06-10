package me.ponktacology.practice.match.team;

import lombok.Data;

@Data
public class TeamMatchStatistics {
    private int bridgeScore;

    public int increaseBridgeScore() {
        return ++bridgeScore;
    }
}
