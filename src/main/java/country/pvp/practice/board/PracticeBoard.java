package country.pvp.practice.board;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

public class PracticeBoard extends PlayerListener {

    public static final String TITLE = ChatColor.YELLOW.toString() + ChatColor.BOLD + "Practice";
    private final Map<PracticePlayer, FastBoard> boards = Maps.newConcurrentMap();
    private final BoardProvider provider;

    @Inject
    public PracticeBoard(PlayerManager playerManager, BoardProvider provider) {
        super(playerManager);
        this.provider = provider;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FastBoard board = new FastBoard(player);
        board.updateTitle(TITLE);
        PracticePlayer practicePlayer = get(event);
        updateBoard(practicePlayer, board);
        this.boards.put(practicePlayer, board);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PracticePlayer practicePlayer = get(event);
        FastBoard board = this.boards.remove(practicePlayer);
        if (board != null) {
            board.delete();
        }
    }

    public void update() {
        for (Map.Entry<PracticePlayer, FastBoard> entry : this.boards.entrySet()) {
            updateBoard(entry.getKey(), entry.getValue());
        }
    }

    private void updateBoard(PracticePlayer player, FastBoard board) {
        board.updateLines(provider.provide(player));
    }
}
