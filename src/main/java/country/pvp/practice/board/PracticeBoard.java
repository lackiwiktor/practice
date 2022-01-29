package country.pvp.practice.board;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
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
    private final Map<PlayerSession, FastBoard> boards = Maps.newConcurrentMap();
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
        PlayerSession playerSession = get(event);
        updateBoard(playerSession, board);
        this.boards.put(playerSession, board);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerSession playerSession = get(event);
        FastBoard board = this.boards.remove(playerSession);
        if (board != null) {
            board.delete();
        }
    }

    public void update() {
        for (Map.Entry<PlayerSession, FastBoard> entry : this.boards.entrySet()) {
            try {
                updateBoard(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateBoard(PlayerSession player, FastBoard board) {
        board.updateLines(provider.provide(player));
    }
}
