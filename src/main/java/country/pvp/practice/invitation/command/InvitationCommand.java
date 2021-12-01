package country.pvp.practice.invitation.command;

import com.google.inject.Inject;
import country.pvp.practice.command.PlayerCommand;
import country.pvp.practice.invitation.Invitation;
import country.pvp.practice.invitation.InvitationManager;
import country.pvp.practice.invitation.InvitationService;
import country.pvp.practice.message.Messager;
import country.pvp.practice.player.PlayerManager;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Name;
import me.vaperion.blade.command.annotation.Sender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class InvitationCommand extends PlayerCommand {

    private final InvitationManager invitationManager;
    private final InvitationService invitationService;

    @Inject
    public InvitationCommand(PlayerManager playerManager, InvitationManager invitationManager, InvitationService invitationService) {
        super(playerManager);
        this.invitationManager = invitationManager;
        this.invitationService = invitationService;
    }

    @Command("acceptinvitation")
    public void accept(@Sender Player sender, @Name("id") UUID uuid) {
        Optional<Invitation> invitationOptional = invitationManager.get(uuid);

        if (!invitationOptional.isPresent()) {
            Messager.messageError(sender, "You have not received an invite from this player or it has expired.");
            return;
        }

        Invitation invitation = invitationOptional.get();

        invitationService.accept(invitation);
    }

    @Command("declineinvitation")
    public void decline(@Sender Player sender, @Name("id") UUID uuid) {
        Optional<Invitation> invitationOptional = invitationManager.get(uuid);

        if (!invitationOptional.isPresent()) {
            Messager.messageError(sender, "You have not received an invite from this player or it has expired.");
            return;
        }

        Invitation invitation = invitationOptional.get();

        invitationService.decline(invitation);
    }
}
