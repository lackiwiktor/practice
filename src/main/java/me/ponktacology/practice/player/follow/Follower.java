package me.ponktacology.practice.player.follow;

import lombok.Data;
import me.ponktacology.practice.player.PracticePlayer;

@Data
public class Follower {
    private final PracticePlayer followerPlayer;
    private final boolean silent;
}
