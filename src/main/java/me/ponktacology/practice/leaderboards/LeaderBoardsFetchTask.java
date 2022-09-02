package me.ponktacology.practice.leaderboards;

import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.ladder.LadderService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LeaderBoardsFetchTask implements Runnable {

    private final LadderService ladderService;
    private final LeaderBoardsService leaderBoardsService;

    @Override
    public void run() {
        for (Ladder ladder : ladderService.getAllLadders()) {
            leaderBoardsService.fetchTopPlayers(ladder);
        }
    }
}
