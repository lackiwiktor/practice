package me.ponktacology.practice.party;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.ponktacology.practice.Cache;
import me.ponktacology.practice.PracticePreconditions;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.party.command.PartyCommands;
import me.ponktacology.practice.party.listener.PartyListener;
import me.ponktacology.practice.player.PracticePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PartyService extends Service implements Cache {

  private final Set<Party> parties = Sets.newHashSet();
  private final Map<PracticePlayer, Party> playerToPartyMap = Maps.newHashMap();

  @Override
  public void configure() {
    addListener(new PartyListener(this));
    addCommand(new PartyCommands(this));
    registerTask(
        () -> {
          for (Party party : parties) {
            party.invalidateInviteRequests();
            party.invalidateDuelRequests();
          }
        },
        1L,
        TimeUnit.SECONDS,
        true);
  }

  public void remove(Party party) {
    parties.remove(party);
  }

  public Set<Party> getAll() {
    return ImmutableSet.copyOf(parties);
  }

  public Party createParty(PracticePlayer leader) {
    if (!PracticePreconditions.canCreateParty(leader)) {
      return null;
    }
    Party party = new Party(leader);
    parties.add(party);
    return party;
  }

  public void updatePlayerParty(PracticePlayer player, @Nullable Party party) {
    if (party == null) {
      playerToPartyMap.remove(player);
      return;
    }

    playerToPartyMap.put(player, party);
  }

  public boolean hasParty(PracticePlayer player) {
    return playerToPartyMap.containsKey(player);
  }

  public Party getPlayerParty(PracticePlayer player) {
    Preconditions.checkArgument(hasParty(player), "player is not in a party");
    return playerToPartyMap.get(player);
  }

  @Override
  public int getSize() {
    return playerToPartyMap.size();
  }
}
