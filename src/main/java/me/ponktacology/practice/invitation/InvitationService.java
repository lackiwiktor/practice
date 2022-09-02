package me.ponktacology.practice.invitation;

import com.google.common.collect.Maps;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.invitation.command.InvitationCommands;
import me.ponktacology.practice.player.PlayerService;
import me.ponktacology.practice.player.PracticePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InvitationService extends Service {

  private final Map<UUID, Invitation> invitations = Maps.newConcurrentMap();

  @Override
  public void configure() {
    addCommand(new InvitationCommands(this));

    Runnable invitationInvalidateTask = () -> {
      invalidate();

      for (PracticePlayer practicePlayer : Practice.getService(PlayerService.class).getAll()) {
        if (!practicePlayer.isOnline()) continue;
        practicePlayer.invalidateDuelRequests();
      }
    };

    registerTask(invitationInvalidateTask, 2, TimeUnit.SECONDS, true);
  }

  public void add(Invitation invitation) {
    invitations.put(invitation.getId(), invitation);
  }

  public Optional<Invitation> get(UUID uuid) {
    return Optional.ofNullable(invitations.get(uuid));
  }

  public void remove(Invitation invitation) {
    invitations.remove(invitation.getId());
  }

  public void invalidate() {
    invitations.entrySet().removeIf(entry -> entry.getValue().hasExpired());
  }

  public void invite(Invitable invitee, Invitation invitation) {
    add(invitation);

    TextComponent.Builder builder = Component.text(invitation.getMessage()).toBuilder();

    builder.append(Component.newline());
    builder.append(Component.text("Click "));
    builder.append(
        Component.text("YES")
            .clickEvent(
                ClickEvent.clickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    String.format("/acceptinvitation %s", invitation.getId().toString()))));
    builder.append(Component.text(" to accept or "));
    builder.append(
        Component.text("NO")
            .clickEvent(
                ClickEvent.clickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    String.format("/declineinvitation %s", invitation.getId().toString()))));

    invitee.receiveInvite(builder.build());
  }

  public void accept(Invitation invitation) {
    if (invitation.accept()) {
      remove(invitation);
    }
  }

  public void decline(Invitation invitation) {
    invitation.decline();
    remove(invitation);
  }
}
