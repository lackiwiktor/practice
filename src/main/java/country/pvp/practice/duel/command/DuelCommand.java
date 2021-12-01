package country.pvp.practice.duel.command;

import com.google.inject.Inject;
import country.pvp.practice.command.PlayerCommand;
import country.pvp.practice.duel.PlayerDuelRequest;
import country.pvp.practice.duel.PlayerDuelService;
import country.pvp.practice.kit.editor.KitChooseMenuProvider;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.message.Messager;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Name;
import me.vaperion.blade.command.annotation.Optional;
import me.vaperion.blade.command.annotation.Sender;
import org.bukkit.entity.Player;

public class DuelCommand extends PlayerCommand {

    private final PlayerDuelService playerDuelService;
    private final KitChooseMenuProvider kitChooseMenuProvider;

    @Inject
    public DuelCommand(PlayerManager playerManager, PlayerDuelService playerDuelService, KitChooseMenuProvider kitChooseMenuProvider) {
        super(playerManager);
        this.playerDuelService = playerDuelService;
        this.kitChooseMenuProvider = kitChooseMenuProvider;
    }

    @Command("duel")
    public void duel(@Sender Player sender, @Name("player") PracticePlayer invitee, @Optional @Name("ladder") Ladder ladder) {
        PracticePlayer inviter = get(sender);

        if (inviter.equals(invitee)) {
            Messager.messageError(inviter, "You can't invite yourself for a duel.");
            return;
        }

        if (ladder != null) {
            playerDuelService.inviteForDuel(inviter, invitee, ladder);
        } else {
            kitChooseMenuProvider
                    .provide((l) -> playerDuelService.inviteForDuel(inviter, invitee, l))
                    .openMenu(sender);
        }
    }

    @Command("accept")
    public void accept(@Sender Player sender, @Name("player") PracticePlayer player) {
        PracticePlayer invitee = get(sender);

        if (!invitee.hasDuelRequest(player)) {
            Messager.messageError(sender, "You have not received duel request from this player.");
            return;
        }

        PlayerDuelRequest duelRequest = invitee.getDuelRequest(player);
        playerDuelService.acceptInvite(invitee, duelRequest);
    }
}
