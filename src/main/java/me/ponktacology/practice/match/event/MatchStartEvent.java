package me.ponktacology.practice.match.event;

import me.ponktacology.practice.match.Match;

public class MatchStartEvent extends MatchEvent {
  public MatchStartEvent(Match match) {
    super(match);
  }
}
