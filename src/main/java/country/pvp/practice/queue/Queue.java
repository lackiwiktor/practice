package country.pvp.practice.queue;

import com.mongodb.assertions.Assertions;
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
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.player.data.PlayerState;
import country.pvp.practice.match.team.SoloTeam;
import lombok.Data;

@Data
public class Queue {

    private final java.util.Queue<PlayerQueueData> entries = new NonDuplicatePriorityQueue<>();

    private final Ladder ladder;
    private final boolean ranked;
    private final ItemBarManager itemBarManager;
    private final ArenaManager arenaManager;
    private final MatchProvider matchProvider;

    public void addPlayer( PracticePlayer player) {
        PlayerQueueData entry = new PlayerQueueData(player, this);
        entries.add(entry);
        player.setState(PlayerState.QUEUING, entry);
        itemBarManager.apply(ItemBarType.QUEUE, player);
        Messager.message(player, Messages.PLAYER_JOINED_QUEUE.match(
                new MessagePattern("{queue}", ladder.getDisplayName()),
                new MessagePattern("{ranked}", ranked ? "&branked" : "&dunranked")));
    }

    public void removePlayer( PracticePlayer player, boolean leftQueue) {
        player.removeStateData();

        if (leftQueue) {
            entries.removeIf(it -> it.getPlayer().equals(player));
            player.setState(PlayerState.IN_LOBBY);
            itemBarManager.apply(ItemBarType.LOBBY, player);
            Messager.message(player, Messages.PLAYER_LEFT_QUEUE);
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

        Arena arena = arenaManager.getRandom();

        Messager.messageSuccess(queueData1, Messages.QUEUE_FOUND_OPPONENT.match("{player}", queueData2.getName()));
        Messager.messageSuccess(queueData2, Messages.QUEUE_FOUND_OPPONENT.match("{player}", queueData1.getName()));

        createMatch(queueData1, queueData2, arena).start();
    }

    public int size() {
        return entries.size();
    }

    private Match createMatch( PlayerQueueData queueData1, PlayerQueueData queueData2, Arena arena) {
        return matchProvider.provide(ladder, arena, ranked, new SoloTeam(queueData1.getPlayer()), new SoloTeam(queueData2.getPlayer()));
    }
}
