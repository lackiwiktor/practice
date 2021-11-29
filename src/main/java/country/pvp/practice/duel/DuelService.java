package country.pvp.practice.duel;

import com.google.inject.Inject;
import country.pvp.practice.kit.editor.KitChooseMenuProvider;
import country.pvp.practice.match.MatchProvider;
import country.pvp.practice.match.team.SoloTeam;
import country.pvp.practice.message.Messager;
import country.pvp.practice.message.component.ChatComponentBuilder;
import country.pvp.practice.message.component.ChatHelper;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DuelService {

    private final KitChooseMenuProvider kitChooseMenuProvider;
    private final MatchProvider matchProvider;

    public void invite(PracticePlayer player, RematchData rematchData) {
        PracticePlayer other = rematchData.getPlayer();

        if (!canInvite(player, other)) return;

        DuelRequest request = new DuelRequest(player, rematchData.getLadder());
        sendInvite(player, other, request);
    }

    public void invite(PracticePlayer player, PracticePlayer other) {
        if (!canInvite(player, other)) return;

        kitChooseMenuProvider.provide((ladder) -> sendInvite(player, other, new DuelRequest(player, ladder))).openMenu(player.getPlayer());
    }

    private void sendInvite(PracticePlayer player, PracticePlayer other, DuelRequest request) {
        if (player.hasDuelRequest(other)) {
            DuelRequest duelRequest = player.getDuelRequest(other);

            if (duelRequest.getLadder().equals(request.getLadder())) {
                acceptDuel(other, request);
                return;
            }
        }

        other.addDuelRequest(request);
        Messager.messageSuccess(player, "Successfully sent duel to " + player.getName() + ".");
        ChatComponentBuilder builder = new ChatComponentBuilder(ChatColor.GREEN + "You have been invited for a duel by " + ChatColor.WHITE + player.getName() + ChatColor.GREEN + ".");
        builder.append("\n");
        builder.append(new ChatComponentBuilder(ChatColor.GOLD.toString() + ChatColor.BOLD + "Click here to accept.").attachToEachPart(ChatHelper.click("/accept " + player.getName())).create());
        other.sendComponent(builder.create());
    }

    private boolean canInvite(PracticePlayer player, PracticePlayer other) {
        if (!player.isInLobby()) {
            Messager.messageError(player, "You must be in lobby in order to duel someone.");
            return false;
        }

        if (!other.isInLobby()) {
            Messager.messageError(player, "This player is busy right now.");
            return false;
        }

        if (other.hasDuelRequest(player)) {
            Messager.messageError(player, "Wait before dueling this player again.");
        }

        return true;
    }

    public void acceptDuel(PracticePlayer player, DuelRequest request) {
        if (!player.isInLobby()) {
            Messager.messageError(player, "You must be in lobby in order to accept duel request.");
            return;
        }

        PracticePlayer requestingPracticePlayer = request.getPlayer();

        player.invalidateRequest(request);

        if (!requestingPracticePlayer.isOnline()) {
            Messager.messageError(player, "This player is not online.");
            return;
        }

        if (!requestingPracticePlayer.isInLobby()) {
            Messager.messageError(player, "This player is not in the lobby.");
            return;
        }

        matchProvider.provide(request.getLadder(), false, true, SoloTeam.of(player), SoloTeam.of(requestingPracticePlayer)).start();
    }
}
