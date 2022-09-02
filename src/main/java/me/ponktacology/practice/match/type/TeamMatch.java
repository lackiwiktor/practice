package me.ponktacology.practice.match.type;

import com.google.common.collect.Lists;
import me.ponktacology.practice.arena.match.MatchArenaCopy;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.ladder.LadderType;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.MatchState;
import me.ponktacology.practice.match.info.PlayerInfoTracker;
import me.ponktacology.practice.match.snapshot.InventorySnapshot;
import me.ponktacology.practice.match.statistics.PlayerStatisticsTracker;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.match.team.type.SoloTeam;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.message.FormatUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.List;


/**
 * Team vs Team match
 */
public class TeamMatch extends Match {

    private final Team teamA;
    private final Team teamB;

    public TeamMatch(Ladder ladder,
                     boolean ranked,
                     boolean duel,
                     MatchArenaCopy arena,
                     Team teamA,
                     Team teamB) {
        super(ladder, ranked, duel, arena);
        this.teamA = teamA;
        this.teamB = teamB;
    }


    @Override
    protected Location getSpawnLocation(Team team) {
        return team.equals(teamA) ? getArena().getSpawnLocation1() : getArena().getSpawnLocation2();
    }

    @Override
    public List<Team> getTeams() {
        return Lists.newArrayList(teamA, teamB);
    }

    // Only one team can be dead at a time,
    // so it's certain that the other is alive,
    // which lets you pick a winner
    @Override
    public boolean canEndRound() {
        return getTeams().stream().anyMatch(this::isTeamDead);
    }

    @Override
    public InventorySnapshot createInventorySnapshot(PracticePlayer player) {
        InventorySnapshot snapshot = super.createInventorySnapshot(player);

        Team team = getTeam(player);
        Team opponent = getOpponent(team);

        if (opponent instanceof SoloTeam) {
            SoloTeam soloOpponent = (SoloTeam) opponent;
            PracticePlayer opponentPlayer = soloOpponent.getPracticePlayer();
            InventorySnapshot inventorySnapshot = getInventorySnapshotTracker().get(opponentPlayer);

            if (inventorySnapshot != null) {
                inventorySnapshot.setOpponent(snapshot.getId());
                snapshot.setOpponent(inventorySnapshot.getId());
            }
        }

        return snapshot;
    }

    @Override
    public List<String> getBoard(PracticePlayer player) {
        List<String> lines = Lists.newArrayList();

        if (player.isSpectating()) {
            Team playerTeam = teamA;
            Team opponentTeam = teamB;

            String teamName = playerTeam.getName();
            String teamNameOpponent = opponentTeam.getName();

            if (playerTeam instanceof SoloTeam && opponentTeam instanceof SoloTeam) {
                SoloTeam soloTeam = (SoloTeam) playerTeam;
                SoloTeam soloTeamOpponent = (SoloTeam) opponentTeam;

                lines.add(ChatColor.GREEN + teamName + ChatColor.WHITE + " Ping: " + ChatColor.WHITE + soloTeam.getPing());
                lines.add(ChatColor.BLUE + teamNameOpponent + ChatColor.WHITE + " Ping: " + ChatColor.WHITE + soloTeamOpponent.getPing());
            } else {
                lines.add(ChatColor.GREEN + "Team " + teamName + ": " + ChatColor.WHITE + getAlivePlayersCount(playerTeam) + "/" + playerTeam.size());
                lines.add(ChatColor.BLUE + "Team " + teamNameOpponent + ": " + ChatColor.WHITE + getAlivePlayersCount(playerTeam) + "/" + opponentTeam.size());
            }

            lines.add(" ");
        } else {
            Team playerTeam = getTeam(player);
            Team opponentTeam = getOpponent(playerTeam);


            if (playerTeam instanceof SoloTeam && opponentTeam instanceof SoloTeam) {
                if (getState() == MatchState.STARTING) {
                    lines.add(ChatColor.WHITE + "Opponent: " + ChatColor.YELLOW + opponentTeam.getName());
                    lines.add(" ");
                }

                SoloTeam soloTeam = (SoloTeam) playerTeam;
                SoloTeam soloTeamOpponent = (SoloTeam) opponentTeam;

                lines.add(ChatColor.WHITE + "Your Ping: " + ChatColor.YELLOW + soloTeam.getPing());
                lines.add(ChatColor.WHITE + "Their Ping: " + ChatColor.YELLOW + soloTeamOpponent.getPing());
                lines.add(" ");

                PlayerStatisticsTracker statisticsTracker = getStatisticsTracker();
                if (getLadderType() == LadderType.BOXING) {
                    lines.add("Hits: " + statisticsTracker.getHits(soloTeam.getPracticePlayer()));
                    lines.add("Hits: " + statisticsTracker.getHits(soloTeamOpponent.getPracticePlayer()));
                }
            } else if (getPlayersCount() > 5) {
                lines.add(ChatColor.GREEN + "Team: " + ChatColor.WHITE + getAlivePlayersCount(playerTeam) + "/" + playerTeam.size());
                lines.add(ChatColor.RED + "Opponents: " + ChatColor.WHITE + getAlivePlayersCount(opponentTeam) + "/" + opponentTeam.size());
                lines.add(" ");
            } else {
                lines.add(ChatColor.GREEN + "Team: " + ChatColor.WHITE +getAlivePlayersCount(playerTeam) + "/" + playerTeam.size());
                PlayerInfoTracker infoTracker = getInfoTracker();
                for (PracticePlayer session : playerTeam.getPlayers()) {
                    boolean isOnline = session.isOnline();
                    String line = " "
                            + ((!isOnline || !infoTracker.isAlive(session)) ? (ChatColor.STRIKETHROUGH + ChatColor.GRAY.toString()) : "")
                            + ChatColor.DARK_GREEN + session.getName()
                            + (isOnline && infoTracker.isAlive(session) ? (ChatColor.WHITE + " " + FormatUtil.formatHealthWithHeart(session.getHealth())) : "");
                    lines.add(line);
                }

                lines.add(" ");
                lines.add(ChatColor.RED + "Opponents: " + ChatColor.WHITE + getAlivePlayersCount(opponentTeam) + "/" + opponentTeam.size());
            }
        }

        return lines;
    }

    public ChatColor getRelativeColor(Team team) {
        return teamA.equals(team) ? ChatColor.GREEN : ChatColor.BLUE;
    }

    private Team getOpponent(Team team) {
        return team.equals(teamA) ? teamB : teamA;
    }
}
