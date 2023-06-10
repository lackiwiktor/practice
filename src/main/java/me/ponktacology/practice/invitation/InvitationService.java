package me.ponktacology.practice.invitation;

import com.google.common.collect.Maps;
import ga.windpvp.windspigot.commons.ClickableBuilder;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.invitation.command.InvitationCommands;
import me.ponktacology.practice.player.PlayerService;
import me.ponktacology.practice.player.PracticePlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InvitationService extends Service {

  private final Map<UUID, Invitation> invitations = Maps.newConcurrentMap();

  @Override
  public void configure() {
    addCommand(new InvitationCommands(this));

    Runnable invitationInvalidateTask =
        () -> {
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

    TextComponent builder = new ClickableBuilder(invitation.getMessage()).build();

    builder.addExtra("\n");
    builder.addExtra("Click ");
    builder.addExtra(
        new ClickableBuilder("YES")
            .setClick(
                String.format("/acceptinvitation %s", invitation.getId().toString()),
                ClickEvent.Action.RUN_COMMAND)
            .build());
    builder.addExtra(" to accept or ");
    builder.addExtra(
        new ClickableBuilder("NO")
            .setClick(
                String.format("/declineinvitation %s", invitation.getId().toString()),
                ClickEvent.Action.RUN_COMMAND)
            .build());
    invitee.receiveInvite(builder);
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
