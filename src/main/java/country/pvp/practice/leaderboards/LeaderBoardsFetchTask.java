package country.pvp.practice.leaderboards;

import com.google.inject.Inject;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.ladder.LadderManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class LeaderBoardsFetchTask implements Runnable {

    private final LadderManager ladderManager;
    private final LeaderBoardsService leaderBoardsService;

    @Override
    public void run() {
        for (Ladder ladder : ladderManager.getAll()) {
            leaderBoardsService.fetchTopPlayers(ladder);
        }
    }
}
