package country.pvp.practice.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor
public enum Messages {
    PLAYER_JOINED_QUEUE("PLAYER_JOINED_QUEUE", "&eYou have joined the {ranked} {queue} &equeue."),
    PLAYER_LEFT_QUEUE("PLAYER_LEFT_QUEUE", "&cYou have left the queue."),
    MATCH_COUNTDOWN("MATCH_COUNTDOWN", "&eMatch will start in &f{time} &eseconds."),
    QUEUE_FOUND_OPPONENT("QUEUE_FOUND_OPPONENT", "&eFound opponent: &f{player}"),
    MATCH_START("MATCH_START", "&6Match has started!"),
    MATCH_WON("MATCH_WON", "&aYour team has won!"),
    MATCH_LOST("MATCH_LOST", "&cYour team has lost!"),
    MATCH_CANCELLED("MATCH_CANCELLED", "&4This match has been cancelled. ({reason})"),
    MATCH_PLAYER_DISCONNECT("MATCH_PLAYER_DISCONNECT", "&cPlayer {player} &chas disconnected!"),
    MATCH_PLAYER_STARTED_SPECTATING("MATCH_PLAYER_STARTED_SPECTATING", "&ePlayer &f{player}&e started spectating this match."),
    MATCH_PLAYER_STOPPED_SPECTATING("MATCH_PLAYER_STOPPED_SPECTATING", "&ePlayer &f{player}&e is no longer spectating this match."),
    MATCH_PLAYER_KILLED_BY_UNKNOWN("MATCH_PLAYER_KILLED_BY_UNKNOWN", "&ePlayer {player} &ehas died!"),
    MATCH_PLAYER_KILLED_BY_PLAYER("MATCH_PLAYER_KILLED_BY_PLAYER", "&ePlayer {player} &ehas been killed by {killer}&e!");

    private final @NotNull String name;
    private final @NotNull String value;

    public @NotNull String get() {
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
