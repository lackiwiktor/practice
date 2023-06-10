package me.ponktacology.practice.match.event;

import lombok.Getter;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.player.PracticePlayer;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@Getter
public class MatchPlayerDamageByPlayerEvent extends MatchEvent {

  private final PracticePlayer damaged;
  private final PracticePlayer damager;
  private final EntityDamageByEntityEvent bukkitEvent;

  public MatchPlayerDamageByPlayerEvent(
      Match match,
      PracticePlayer damaged,
      PracticePlayer damager,
      EntityDamageByEntityEvent bukkitEvent) {
    super(match);
    this.damaged = damaged;
    this.damager = damager;
    this.bukkitEvent = bukkitEvent;
  }
}
