package me.ponktacology.practice.match.event;

import me.ponktacology.practice.match.Match;

public class MatchStartCountdownEvent extends MatchEvent {
  public MatchStartCountdownEvent(Match match) {
    super(match);
  }
}
