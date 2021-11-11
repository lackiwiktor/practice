package country.pvp.practice.queue;

import com.google.common.collect.Queues;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.arena.ArenaManager;
import country.pvp.practice.itembar.ItemBarManager;
import country.pvp.practice.itembar.ItemBarType;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchProvider;
import country.pvp.practice.message.MessagePattern;
import country.pvp.practice.message.Messager;
import country.pvp.practice.message.Messages;
import country.pvp.practice.player.data.PlayerState;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.team.SoloTeam;
import lombok.Data;

import java.util.PriorityQueue;

@Data
public class Queue {

    private final PriorityQueue<QueueData> entries = Queues.newPriorityQueue();

    private final Ladder ladder;
    private final boolean ranked;
    private final ItemBarManager itemBarManager;
    private final ArenaManager arenaManager;
    private final MatchProvider matchProvider;

    public void addPlayer(PracticePlayer player) {
        QueueData entry = new QueueData(player, this);
        entries.add(entry);
        player.setState(PlayerState.QUEUING);
        player.setStateData(PlayerState.QUEUING, entry);
        itemBarManager.apply(ItemBarType.QUEUE, player);
        Messager.message(player, Messages.PLAYER_JOINED_QUEUE,
                new MessagePattern("{queue}", ladder.getDisplayName()),
                new MessagePattern("{ranked}", ranked ? "ranked" : "unranked"));
    }

    public void removePlayer(PracticePlayer player, boolean leftQueue) {
        entries.removeIf(it -> it.getPlayer().equals(player));
        player.removeStateData(PlayerState.QUEUING);

        if (leftQueue) {
            player.setState(PlayerState.IN_LOBBY);
            itemBarManager.apply(ItemBarType.LOBBY, player);
            Messager.message(player, Messages.PLAYER_LEFT_QUEUE);
        }
    }

    public void tick() {
        if (entries.size() < 2) return;

        QueueData queueData1 = entries.poll();
        QueueData queueData2 = entries.poll();

        removePlayer(queueData1.getPlayer(), false);
        removePlayer(queueData2.getPlayer(), false);

        Arena arena = arenaManager.getRandom();

        createMatch(queueData1, queueData2, arena).startMatch();
    }

    public int size() {
        return entries.size();
    }

    private Match createMatch(QueueData queueData1, QueueData queueData2, Arena arena) {
        return matchProvider.provide(ladder, arena, ranked, new SoloTeam(queueData1.getPlayer()), new SoloTeam(queueData2.getPlayer()));
    }
}
