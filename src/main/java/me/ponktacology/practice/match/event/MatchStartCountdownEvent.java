package me.ponktacology.practice.match.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.util.BasicEvent;
import me.ponktacology.practice.match.Match;

@RequiredArgsConstructor
public class MatchStartCountdownEvent extends BasicEvent {
  @Getter private final Match match;
}
