package country.pvp.practice.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum Messages {

    PLAYER_JOINED_QUEUE("&eYou have joined the {ranked} {queue} &equeue."),
    PLAYER_LEFT_QUEUE("&cYou have left the queue."),
    MATCH_COUNTDOWN("&eMatch will start in &f{time} &eseconds."),
    QUEUE_FOUND_OPPONENT("&eFound opponent: &f{player}"),
    MATCH_START("&6Match has started!"),
    MATCH_CANCELLED("&4This match has been cancelled. ({reason})"),
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
    PARTY_PLAYER_WAS_KICKED("Player {player} was kicked from the party.");

    private final String value;

    public String get() {
        return value;
    }

    public String match(String placeholder, Object value) {
        return match(new MessagePattern(placeholder, value));
    }

    public String match(MessagePattern @Nullable ... patterns) {
        if (patterns == null) return value;

        String matched = value;

        for (MessagePattern pattern : patterns) {
            matched = pattern.translate(matched);
        }

        return matched;
    }
}
