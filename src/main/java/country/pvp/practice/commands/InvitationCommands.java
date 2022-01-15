package country.pvp.practice.commands;

import com.google.inject.Inject;
import country.pvp.practice.invitation.Invitation;
import country.pvp.practice.invitation.InvitationManager;
import country.pvp.practice.invitation.InvitationService;
import country.pvp.practice.util.message.Sender;
import country.pvp.practice.player.PlayerManager;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Name;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class InvitationCommands extends PlayerCommands {

    private final InvitationManager invitationManager;
    private final InvitationService invitationService;

    @Inject
    public InvitationCommands(PlayerManager playerManager, InvitationManager invitationManager, InvitationService invitationService) {
        super(playerManager);
        this.invitationManager = invitationManager;
        this.invitationService = invitationService;
    }

    @Command("acceptinvitation")
    public void accept(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("id") UUID uuid) {
        Optional<Invitation> invitationOptional = invitationManager.get(uuid);

        if (!invitationOptional.isPresent()) {
            Sender.messageError(sender, "You have not received an invite from this player or it has expired.");
            return;
        }

        Invitation invitation = invitationOptional.get();

        invitationService.accept(invitation);
    }

    @Command("declineinvitation")
    public void decline(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("id") UUID uuid) {
        Optional<Invitation> invitationOptional = invitationManager.get(uuid);

        if (!invitationOptional.isPresent()) {
            Sender.messageError(sender, "You have not received an invite from this player or it has expired.");
            return;
        }

        Invitation invitation = invitationOptional.get();

        invitationService.decline(invitation);
    }
}
