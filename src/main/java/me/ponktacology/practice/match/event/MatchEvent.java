package me.ponktacology.practice.match.event;

import lombok.Data;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.util.BasicEvent;

@Data
public class MatchEvent extends BasicEvent {
    private final Match match;
}
