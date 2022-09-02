package me.ponktacology.practice.match;

import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.player.PracticePlayer;
import lombok.Data;

@Data
public class RematchData {

    private final PracticePlayer player;
    private final Ladder ladder;

    @Override
    public String toString() {
        return "RematchData{" +
                "player=" + player.getName() +
                "ladder=" + ladder.getName() +
                '}';
    }
}
