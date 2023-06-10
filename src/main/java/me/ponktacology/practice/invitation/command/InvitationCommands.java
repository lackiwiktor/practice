package me.ponktacology.practice.invitation.command;

import me.ponktacology.practice.commands.PlayerCommands;
import me.ponktacology.practice.invitation.Invitation;
import me.ponktacology.practice.invitation.InvitationService;
import me.ponktacology.practice.util.message.Messenger;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.annotation.argument.Name;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class InvitationCommands extends PlayerCommands {

  private final InvitationService invitationService;

  @Command("acceptinvitation")
  public void accept(@Sender Player sender, @Name("id") UUID uuid) {
    Optional<Invitation> invitationOptional = invitationService.get(uuid);

    if (!invitationOptional.isPresent()) {
      Messenger.messageError(
          sender, "You have not received an invite from this player or it has expired.");
      return;
    }

    Invitation invitation = invitationOptional.get();

    invitationService.accept(invitation);
  }

  @Command("declineinvitation")
  public void decline(@Sender Player sender, @Name("id") UUID uuid) {
    Optional<Invitation> invitationOptional = invitationService.get(uuid);

    if (!invitationOptional.isPresent()) {
      Messenger.messageError(
          sender, "You have not received an invite from this player or it has expired.");
      return;
    }

    Invitation invitation = invitationOptional.get();

    invitationService.decline(invitation);
  }
}
