package me.ponktacology.practice.party.listener;

import me.ponktacology.practice.party.Party;
import me.ponktacology.practice.party.PartyService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PartyListener extends PracticePlayerListener {

  private final PartyService partyService;

  @EventHandler
  public void playerQuitEvent(PlayerQuitEvent event) {
    PracticePlayer practicePlayer = get(event);

    if (partyService.hasParty(practicePlayer)) {
      Party party = partyService.getPlayerParty(practicePlayer);
      party.leaveFromParty(practicePlayer, Party.RemoveReason.DISCONNECTED);
    }
  }
}
