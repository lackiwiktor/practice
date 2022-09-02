package me.ponktacology.practice.board;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fr.mrmicky.fastboard.FastBoard;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.event.EventService;
import me.ponktacology.practice.event.EventType;
import me.ponktacology.practice.event.type.Thimble;
import me.ponktacology.practice.event.type.Tournament;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.player.PlayerService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.queue.StateQueueData;
import me.ponktacology.practice.util.TimeUtil;
import me.ponktacology.practice.util.message.Bars;
import me.ponktacology.practice.util.message.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ScoreboardService extends Service {

  public static final String TITLE = ChatColor.YELLOW.toString() + ChatColor.BOLD + "Practice";
  private final Map<PracticePlayer, FastBoard> boards = Maps.newConcurrentMap();

  @Override
  protected void configure() {
    addListener(new ScoreboardListener(this));

    for (PracticePlayer practicePlayer : Practice.getService(PlayerService.class).getAll()) {
      create(practicePlayer);
    }

    Runnable updateScoreboardTask =
        () -> {
          for (Map.Entry<PracticePlayer, FastBoard> entry : this.boards.entrySet()) {
            try {
              updateBoard(entry.getKey(), entry.getValue());
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        };

    registerTask(updateScoreboardTask, 100L, TimeUnit.MILLISECONDS, true);
  }

  @Override
  public void stop() {
    boards.clear();
  }

  public void create(PracticePlayer practicePlayer) {
    Player player = practicePlayer.getPlayer();
    Preconditions.checkNotNull(player, "player must be online");
    FastBoard board = new FastBoard(player);
    board.updateTitle(TITLE);

    updateBoard(practicePlayer, board);
    this.boards.put(practicePlayer, board);
  }

  public void delete(PracticePlayer practicePlayer) {
    FastBoard board = this.boards.remove(practicePlayer);
    if (board != null) {
      board.delete();
    }
  }

  private void updateBoard(PracticePlayer player, FastBoard board) {
    board.updateLines(getScoreboard(player));
  }

  private List<String> getScoreboard(PracticePlayer player) {
    List<String> lines = Lists.newArrayList();

    MatchService matchService = Practice.getService(MatchService.class);
    EventService eventService = Practice.getService(EventService.class);

    switch (player.getState()) {
      case EDITING_KIT:
      case IN_LOBBY:
        lines.add(
            ChatColor.WHITE
                + "Online: "
                + ChatColor.YELLOW
                + Bukkit.getServer().getOnlinePlayers().size());
        lines.add(
            ChatColor.WHITE
                + "Playing: "
                + ChatColor.YELLOW
                + matchService.getPlayersInFightCount());
        lines.add("");

        Tournament tournament = eventService.getEventByType(EventType.TOURNAMENT);
        if (tournament != null) {
          lines.add("Tournament");
          lines.add("Round: " + tournament.getCurrentRound());
          lines.add(
              "Parties: "
                  + tournament.getParticipants().size()
                  + "/"
                  + tournament.getInitialSize());
        }

        Thimble thimble = eventService.getEventByType(EventType.THIMBLE);
        if (thimble != null) {
          lines.add("Thimble");
          lines.add("Round: " + thimble.getRound());
          lines.add(
              "Players: " + thimble.getParticipants().size() + "/" + thimble.getInitialSize());
        }
        break;
      case QUEUING:
        StateQueueData queueData = player.getStateData();
        lines.add(
            ChatColor.WHITE
                + "  Time: "
                + ChatColor.YELLOW
                + TimeUtil.formatTimeMillisToClock(
                    TimeUtil.elapsed(queueData.getJoinedAt().toEpochMilli())));

        if (queueData.isRanked()) {
          int baseElo = player.getElo(queueData.getLadder());
          lines.add(
              ChatColor.WHITE
                  + "  Elo Range: "
                  + ChatColor.YELLOW
                  + Math.max(baseElo - queueData.getEloRangeFactor(), 0)
                  + "-"
                  + (baseElo + queueData.getEloRangeFactor()));
        }

        lines.add(
            ChatColor.WHITE
                + "  Ladder: "
                + ChatColor.YELLOW
                + MessageUtil.color(queueData.getLadderDisplayName()));
        lines.add("");
        break;
      case IN_MATCH:
        Match match = player.getCurrentMatch();
        lines.addAll(match.getBoard(player));
        break;
      case SPECTATING:
        Match spectatingMatch = player.getCurrentlySpectatingMatch();
        lines.addAll(spectatingMatch.getBoard(player));
        break;
    }

    lines.add(0, Bars.SB_BAR);
    lines.add(ChatColor.DARK_GRAY + ChatColor.ITALIC.toString() + "www.practice.com");
    lines.add(Bars.SB_BAR);

    return lines;
  }
}
