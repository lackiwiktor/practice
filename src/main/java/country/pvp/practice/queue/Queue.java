package country.pvp.practice.queue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import country.pvp.practice.arena.ArenaManager;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.MatchProvider;
import country.pvp.practice.match.StandardMatch;
import country.pvp.practice.match.team.SoloTeam;
import country.pvp.practice.message.MessagePattern;
import country.pvp.practice.message.Messager;
import country.pvp.practice.message.Messages;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.player.data.PlayerState;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class Queue {

    private final ItemBarService itemBarService;
    private final ArenaManager arenaManager;
    private final MatchProvider matchProvider;

    private final List<SessionQueueData> entries = Lists.newCopyOnWriteArrayList();

    private final Ladder ladder;
    private final boolean ranked;

    public void addPlayer(PlayerSession player) {
        SessionQueueData entry = new SessionQueueData(player, this);
        entries.add(entry);
        player.setState(PlayerState.QUEUING, entry);
        itemBarService.apply(player);
        Messager.message(player, Messages.PLAYER_JOINED_QUEUE.match(
                new MessagePattern("{queue}", ladder.getDisplayName()),
                new MessagePattern("{ranked}", ranked ? "&branked" : "&dunranked")));
    }

    public void removePlayer(PlayerSession player, boolean leftQueue) {
        entries.removeIf(it -> it.getPlayer().equals(player));

        if (leftQueue) {
            Messager.message(player, Messages.PLAYER_LEFT_QUEUE);
            player.setState(PlayerState.IN_LOBBY);
            itemBarService.apply(player);
        }
    }

    public void tick() {
        if (entries.size() < 2) return;

        Set<SessionQueueData> toRemove = Sets.newHashSet();
        for (SessionQueueData entry : entries) {
            for (SessionQueueData other : entries) {
                if (entry.equals(other) || toRemove.contains(entry) || toRemove.contains(other)) continue;
                if (ranked && !entry.isWithinEloRange(other)) continue;

                Messager.messageSuccess(entry, Messages.QUEUE_FOUND_OPPONENT.match("{player}", other.getName()));
                Messager.messageSuccess(other, Messages.QUEUE_FOUND_OPPONENT.match("{player}", entry.getName()));

                createMatch(entry, other).init();
                toRemove.addAll(ImmutableList.of(entry, other));
                break;
            }
        }

        entries.removeAll(toRemove);
    }

    public int size() {
        return entries.size();
    }

    private StandardMatch createMatch(SessionQueueData queueData1, SessionQueueData queueData2) {
        return matchProvider.provide(ladder, ranked, false, SoloTeam.of(queueData1.getPlayer()), SoloTeam.of(queueData2.getPlayer()));
    }
}
