package me.ponktacology.practice;

import me.ponktacology.practice.util.message.MessagePattern;
import me.ponktacology.practice.util.message.MessageUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum Messages {
  PLAYER_JOINED_QUEUE("&eYou have joined the {ranked} {queue} &equeue."),
  PLAYER_DUEL_INVITATION(
      "&ePlayer &f{player} &7(&f{ping} &ems&7)&e sent you a {ladder} &eduel request."),
  PLAYER_LEFT_QUEUE("&cYou have left the queue."),
  MATCH_COUNTDOWN("&eMatch will start in &f{time} &eseconds."),
  MATCH_START("&6Match has started!"),
  QUEUE_FOUND_OPPONENT("&eFound opponent: &f{player}"),
  MATCH_CANCELLED("&cThis match has been cancelled, {reason}"),
  MATCH_RESULT_OVERVIEW("&6Post-Match Inventories &7(click name to view)"),
  MATCH_RESULT_OVERVIEW_WINNER("&aWinner: "),
  MATCH_RESULT_OVERVIEW_SPLITTER(" &7⎟ "),
  MATCH_RESULT_OVERVIEW_LOSER("&cLoser: "),
  MATCH_RESULT_OVERVIEW_HOVER("&aClick to view inventory of &6{player}&a."),
  MATCH_RESULT_SPECTATORS("&7Spectators: "),
  MATCH_PLAYER_EQUIP_KIT("&eYou have equipped {kit}."),
  MATCH_PLAYER_DISCONNECTED("&c{player} &chas disconnected!"),
  MATCH_PLAYER_PEARL_COOLDOWN("&bYou will be able to use enderpearl again in &f{time}&b."),
  MATCH_PLAYER_PEARL_COOLDOWN_EXPIRED("&aPearl cooldown has expired."),
  MATCH_PLAYER_STARTED_SPECTATING("&f{player}&e started spectating this match."),
  MATCH_PLAYER_STOPPED_SPECTATING("&f{player}&e is no longer spectating this match."),
  MATCH_PLAYER_KILLED_BY_UNKNOWN("&e{player} &ehas died!"),
  MATCH_PLAYER_KILLED_BY_PLAYER("{killer} &ekilled {player}&e!"),
  PARTY_PLAYER_JOINED("Player joined the party."),
  PARTY_PLAYER_LEFT("Player {player} has left the party."),
  PARTY_PLAYER_DISCONNECTED("Player {player} has disconnected."),
  PARTY_PLAYER_WAS_KICKED("Player {player} was kicked from the party."),
  PARTY_DUEL_INVITATION("&eParty &f{party}&e sent you a {ladder} &eduel request."),
  TOURNAMENT_INVALID_PARTY_SIZE("Your party needs to have {size} players to join the tournament."),
  TOURNAMENT_FULL("This tournament is full."),
  TOURNAMENT_COUNTDOWN("&eTournament will start in &f{time} &eseconds."),
  TOURNAMENT_START("&6Tournament has started!"),
  TOURNAMENT_ROUND_COUNTDOWN("&eNext tournament round will start in &f{time} &eseconds."),
  TOURNAMENT_ROUND_START("&6Next tournament round has started!"),
  TOURNAMENT_ODD_PARTY_COUNT(
      "There were an odd number of teams in this round - so your team has advanced to the next round."),
  TOURNAMENT_ALREADY_STARTED("This tournament has already started."),
  STOPPED_FOLLOWING("You are no longer following {player}."),
  STARTED_FOLLOWING("You are now following {player}."),
  PARTY_DISBANDED("Party has been disbanded.");

  private final String value;

  public String get() {
    return MessageUtil.color(value);
  }

  public String match(String placeholder, Object value) {
    return match(new MessagePattern(placeholder, value));
  }

  public String match(MessagePattern @Nullable ... patterns) {
    if (patterns == null) return get();

    String matched = value;

    for (MessagePattern pattern : patterns) {
      matched = pattern.replace(matched);
    }

    return MessageUtil.color(matched);
  }
}
