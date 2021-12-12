package country.pvp.practice.invitation;

import com.google.inject.Inject;
import country.pvp.practice.message.component.ChatComponentBuilder;
import country.pvp.practice.message.component.ChatHelper;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class InvitationService {

    private final InvitationManager invitationManager;

    public <V extends Invitable> void invite(V invitee, Invitation<V> invitation) {
        invitationManager.add(invitation);
        ChatComponentBuilder builder = new ChatComponentBuilder("");
        builder.append(invitation.getMessage() + "\n");
        builder.append(ChatColor.GOLD + "Click ");
        builder.append(
                new ChatComponentBuilder(ChatColor.GREEN.toString() + ChatColor.BOLD + "YES")
                        .attachToEachPart(
                                ChatHelper.click("/acceptinvitation " + invitation.getId().toString()))
                        .create());
        builder.append(ChatColor.GOLD + " to accept or ");
        builder.append(
                new ChatComponentBuilder(ChatColor.RED.toString() + ChatColor.BOLD + "NO")
                        .attachToEachPart(
                                ChatHelper.click("/declineinvitation " + invitation.getId().toString()))
                        .create());
        builder.append(ChatColor.GOLD + " to decline.");
        invitee.receiveInvite(builder.create());
    }

    public void accept(Invitation invitation) {
        invitation.accept();
        invitationManager.remove(invitation);
    }

    public void decline(Invitation invitation) {
        invitation.decline();
        invitationManager.remove(invitation);
    }
}
