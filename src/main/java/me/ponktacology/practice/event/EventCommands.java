package me.ponktacology.practice.event;

import me.ponktacology.practice.Practice;
import me.ponktacology.practice.commands.PlayerCommands;
import me.ponktacology.practice.event.type.Thimble;
import me.ponktacology.practice.event.type.Tournament;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.party.Party;
import me.ponktacology.practice.party.PartyService;
import me.ponktacology.practice.player.PracticePlayer;
import me.vaperion.blade.annotation.argument.Name;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import org.bukkit.entity.Player;

public class EventCommands extends PlayerCommands {

  private Tournament tournament;
  private Thimble thimble;

  @Command("tournament start")
  public void start(@Sender Player sender, @Name("ladder") Ladder ladder) {
    tournament = new Tournament(ladder, 1, 4, 2);
    tournament.init();
  }

  @Command("thimble start")
  public void thimbleStart(@Sender Player sender) {
    thimble = new Thimble(null, 2);
    thimble.init();
  }

  @Command("thimble join")
  public void thimbleJoin(@Sender Player sender) {
    PracticePlayer player = get(sender);
    thimble.add(player);
  }

  @Command("tournament join")
  public void join(@Sender Player sender) {
    if (tournament == null) {
      sender.sendMessage("Tournament is not running");
      return;
    }

    PracticePlayer session = get(sender);
    Party party = session.getParty();
    if (!session.hasParty()) {
      if (tournament.getTeamSize() == 1) {
        Practice.getService(PartyService.class).createParty(session);
        party = session.getParty();
      } else {
        sender.sendMessage("You must be in a party");
        return;
      }
    }

    if (!tournament.add(party)) {
      sender.sendMessage("You can't join");
      return;
    }

    sender.sendMessage("Joined!");
  }
}
