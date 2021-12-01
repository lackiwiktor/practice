package country.pvp.practice.party;

import com.google.inject.Inject;
import country.pvp.practice.invitation.Invitation;
import country.pvp.practice.invitation.InvitationService;
import country.pvp.practice.itembar.ItemBarManager;
import country.pvp.practice.message.Messager;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PartyService {

    private final VisibilityUpdater visibilityUpdater;
    private final ItemBarManager itemBarManager;
    private final InvitationService invitationService;
    private final PartyManager partyManager;

    public void createParty(PracticePlayer leader) {
        if (leader.hasParty()) {
            Messager.messageError(leader, "You already are in a party.");
            return;
        }

        Party party = new Party(leader);
        partyManager.add(party);
        addToParty0(party, leader);
    }

    public void disbandParty(PracticePlayer senderPlayer, Party party) {
        if (!senderPlayer.isPartyLeader()) {
            Messager.messageError(senderPlayer, "You are not the leader of the party.");
            return;
        }

        for (PracticePlayer player : party.getMembers()) {
            player.removeFromParty();
        }

        party.disband();

        for (PracticePlayer player : party.getMembers()) {
            itemBarManager.apply(player);
            for (PracticePlayer other : party.getMembers()) {
                visibilityUpdater.update(player, other);
                visibilityUpdater.update(other, player);
            }
        }

        partyManager.remove(party);
    }

    public void inviteToParty(PracticePlayer inviter, PracticePlayer invitee, Party party) {
        if (party.isPlayerInvited(invitee)) {
            Messager.messageError(inviter, "This player has already been invited to the party.");
            return;
        }

        if (invitee.hasParty()) {
            Messager.messageError(inviter, "This player already has a party.");
            return;
        }

        Invitation invitation = new Invitation("You have been invited to " + party.getName() + " party.", invitee) {
            @Override
            protected void onAccept() {
                acceptInvite(party, invitee);
            }

            @Override
            protected void onDecline() {
                party.removePlayerInvite(invitee);
            }
        };

        party.invite(invitee);
        invitationService.invite(invitee, invitation);
    }

    public void acceptInvite(Party party, PracticePlayer player) {
        addToParty(party, player);
    }

    private void addToParty0(Party party, PracticePlayer player) {
        if (!player.isInLobby()) {
            Messager.messageError(player, "You must be in the lobby in order to accept a party invite");
            return;
        }

        if (party.isDisbanded()) {
            Messager.messageError(player, "This party has disbanded.");
            return;
        }

        if (player.hasParty()) {
            Messager.messageError(player, "You already are in a party.");
            return;
        }

        party.addPlayer(player);
        player.addToParty(party);
        update(player, party);
    }

    private void addToParty(Party party, PracticePlayer player) {
        if (!party.isPlayerInvited(player)) {
            Messager.messageError(player, "You have not been invited to this party or the request has expired.");
            return;
        }

        addToParty0(party, player);
    }

    public void removeFromParty(Party party, PracticePlayer player) {
        party.removePlayer(player);
        player.removeFromParty();
        update(player, party);
    }

    private void update(PracticePlayer player, Party party) {
        for (PracticePlayer partyMember : party.getMembers()) {
            visibilityUpdater.update(player, partyMember);
            visibilityUpdater.update(partyMember, player);
        }

        itemBarManager.apply(player);
    }
}
