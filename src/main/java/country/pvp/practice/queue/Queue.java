package country.pvp.practice.queue;

import com.mongodb.assertions.Assertions;
import country.pvp.practice.arena.ArenaManager;
import country.pvp.practice.concurrent.TaskDispatcher;
import country.pvp.practice.itembar.ItemBarManager;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.PlayerLobbyData;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchProvider;
import country.pvp.practice.match.team.SoloTeam;
import country.pvp.practice.message.MessagePattern;
import country.pvp.practice.message.Messager;
import country.pvp.practice.message.Messages;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.player.data.PlayerState;
import lombok.Data;

@Data
public class Queue {


    private final ItemBarManager itemBarManager;
    private final ArenaManager arenaManager;
    private final MatchProvider matchProvider;

    private final java.util.Queue<PlayerQueueData> entries = new NonDuplicatePriorityQueue<>();

    private final Ladder ladder;
    private final boolean ranked;

    public void addPlayer(PracticePlayer player) {
        PlayerQueueData entry = new PlayerQueueData(player, this);
        entries.add(entry);
        player.setState(PlayerState.QUEUING, entry);
        itemBarManager.apply(player);
        Messager.message(player, Messages.PLAYER_JOINED_QUEUE.match(
                new MessagePattern("{queue}", ladder.getDisplayName()),
                new MessagePattern("{ranked}", ranked ? "&branked" : "&dunranked")));
    }

    public void removePlayer(PracticePlayer player, boolean leftQueue) {
        entries.removeIf(it -> it.getPlayer().equals(player));

        if (leftQueue) {
            Messager.message(player, Messages.PLAYER_LEFT_QUEUE);
            player.setState(PlayerState.IN_LOBBY, new PlayerLobbyData(null));
            itemBarManager.apply(player);
        }
    }

    public void tick() {
        if (entries.size() < 2) return;

        PlayerQueueData queueData1 = entries.poll();
        PlayerQueueData queueData2 = entries.poll();

        removePlayer(queueData1.getPlayer(), false);
        removePlayer(queueData2.getPlayer(), false);

        Assertions.assertFalse(entries.contains(queueData1));
        Assertions.assertFalse(entries.contains(queueData2));

        Messager.messageSuccess(queueData1, Messages.QUEUE_FOUND_OPPONENT.match("{player}", queueData2.getName()));
        Messager.messageSuccess(queueData2, Messages.QUEUE_FOUND_OPPONENT.match("{player}", queueData1.getName()));

        TaskDispatcher.sync(() -> createMatch(queueData1, queueData2).start());
    }

    public int size() {
        return entries.size();
    }

    private Match createMatch(PlayerQueueData queueData1, PlayerQueueData queueData2) {
        return matchProvider.provide(ladder, ranked, false, new SoloTeam(queueData1.getPlayer()), new SoloTeam(queueData2.getPlayer()));
    }
}
