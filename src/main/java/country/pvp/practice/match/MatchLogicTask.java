package country.pvp.practice.match;

import country.pvp.practice.Messages;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
class MatchLogicTask extends BukkitRunnable {

    private final Match match;

    @Setter
    private int nextAction = 6;

    @Override
    public void run() {
        nextAction--;

        if (match.getState() == MatchState.STARTING_ROUND) {
            if (nextAction == 0) {
                match.onRoundStart();
                match.setState(MatchState.PLAYING_ROUND);
                match.broadcast(Messages.MATCH_START);
            } else {
                match.broadcast(Messages.MATCH_COUNTDOWN.match("{time}", nextAction));
            }
        } else if (match.getState() == MatchState.ENDING_ROUND) {
            if (nextAction == 0) {
                if (match.canStartRound()) {
                    match.onRoundStart();
                }
            }
        } else if (match.getState() == MatchState.ENDING_MATCH) {
            if (nextAction == 0) {
                match.end();
                cancel();
            }
        }
    }
}
