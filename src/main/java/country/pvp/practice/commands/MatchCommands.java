package country.pvp.practice.commands;

import com.google.inject.Inject;
import country.pvp.practice.kit.editor.KitChooseMenuProvider;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchProvider;
import country.pvp.practice.match.team.type.SoloTeam;
import country.pvp.practice.util.message.MessagePattern;
import country.pvp.practice.util.message.Sender;
import country.pvp.practice.Messages;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.player.duel.PlayerDuelRequest;
import country.pvp.practice.player.duel.PlayerDuelService;
import me.vaperion.blade.command.annotation.*;
import org.bukkit.entity.Player;

public class MatchCommands extends PlayerCommands {

    private final PlayerDuelService playerDuelService;
    private final KitChooseMenuProvider kitChooseMenuProvider;
    private final MatchProvider matchProvider;

    @Inject
    public MatchCommands(PlayerManager playerManager, PlayerDuelService playerDuelService, KitChooseMenuProvider kitChooseMenuProvider, MatchProvider matchProvider) {
        super(playerManager);
        this.playerDuelService = playerDuelService;
        this.kitChooseMenuProvider = kitChooseMenuProvider;
        this.matchProvider = matchProvider;
    }

    @Command("match cancel")
    public void cancel(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("match") Match match, @Combined @Optional("Cancelled by the staff member") @Name("reason") String reason) {
        match.cancel(reason);
        Sender.messageSuccess(sender, "Successfully cancelled this match.");
    }

    @Command("match ffa")
    public void ffa(@me.vaperion.blade.command.annotation.Sender Player sender, Ladder ladder, PlayerSession p1, PlayerSession p2, PlayerSession p3) {
        matchProvider.provide(ladder, SoloTeam.of(p1), SoloTeam.of(p2), SoloTeam.of(p3)).init();
    }

    @Command("spectate")
    public void specate(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("player") PlayerSession player) {
        PlayerSession playerSession = get(sender);

        if (!playerSession.isInLobby()) {
            Sender.messageError(playerSession, "You must be in lobby in order to spectate someone.");
            return;
        }

        if (!player.isInMatch()) {
            Sender.messageError(playerSession, "This player is not in a match right now.");
            return;
        }

        Match match = player.getCurrentMatch();
        match.startSpectating(playerSession, player);
    }

    @Command("duel")
    public void duel(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("player") PlayerSession invitee, @Optional @Name("ladder") Ladder ladder) {
        PlayerSession inviter = get(sender);

        if (inviter.equals(invitee)) {
            Sender.messageError(inviter, "You can't invite yourself for a duel.");
            return;
        }

        if (ladder != null) {
            playerDuelService.invite(inviter, invitee, ladder, Messages.PLAYER_DUEL_INVITATION.match(
                    new MessagePattern("{player}", inviter.getName()),
                    new MessagePattern("{ping}", inviter.getPing()),
                    new MessagePattern("{ladder}", ladder.getDisplayName())));
        } else {
            kitChooseMenuProvider
                    .provide((l) -> playerDuelService.invite(inviter, invitee, l, Messages.PLAYER_DUEL_INVITATION.match(
                            new MessagePattern("{player}", inviter.getName()),
                            new MessagePattern("{ping}", inviter.getPing()),
                            new MessagePattern("{ladder}", ladder.getDisplayName()))))
                    .openMenu(sender);
        }
    }

    @Command("accept")
    public void accept(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("player") PlayerSession player) {
        PlayerSession invitee = get(sender);

        if (!invitee.hasDuelRequest(player)) {
            Sender.messageError(sender, "You have not received duel request from this player.");
            return;
        }

        PlayerDuelRequest duelRequest = invitee.getDuelRequest(player);
        playerDuelService.acceptInvite(invitee, duelRequest);
    }
}
