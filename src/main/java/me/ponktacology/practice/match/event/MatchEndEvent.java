package me.ponktacology.practice.match.event;

import me.ponktacology.practice.match.Match;

public class MatchEndEvent extends MatchEvent {
    public MatchEndEvent(Match match) {
        super(match);
    }
}
