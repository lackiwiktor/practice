package country.pvp.practice.party;

import com.google.inject.Inject;
import country.pvp.practice.invitation.Invitation;
import country.pvp.practice.invitation.InvitationService;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.message.Messager;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PartyService {

    private final VisibilityUpdater visibilityUpdater;
    private final ItemBarService itemBarService;
    private final InvitationService invitationService;
    private final PartyManager partyManager;

    public void createParty(PlayerSession leader) {
        if (leader.hasParty()) {
            Messager.messageError(leader, "You already are in a party.");
            return;
        }

        Party party = new Party(leader);
        partyManager.add(party);
        addToParty0(party, leader);
    }

    public void disbandParty(PlayerSession senderPlayer, Party party) {
        if (!senderPlayer.isPartyLeader()) {
            Messager.messageError(senderPlayer, "You are not the leader of the party.");
            return;
        }

        for (PlayerSession player : party.getMembers()) {
            player.removeFromParty();
        }

        party.disband();

        for (PlayerSession player : party.getMembers()) {
            itemBarService.apply(player);
            for (PlayerSession other : party.getMembers()) {
                visibilityUpdater.update(player, other);
                visibilityUpdater.update(other, player);
            }
        }

        partyManager.remove(party);
    }

    public void inviteToParty(PlayerSession inviter, PlayerSession invitee, Party party) {
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

    public void acceptInvite(Party party, PlayerSession player) {
        addToParty(party, player);
    }

    private void addToParty0(Party party, PlayerSession player) {
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

    private void addToParty(Party party, PlayerSession player) {
        if (!party.isPlayerInvited(player)) {
            Messager.messageError(player, "You have not been invited to this party or the request has expired.");
            return;
        }

        addToParty0(party, player);
    }

    public void kickFromParty(Party party, PlayerSession session) {
        party.removePlayer(session, Party.RemoveReason.KICKED);

        if (session.isInLobby())
            update(session, party);
    }

    private void update(PlayerSession player, Party party) {
        for (PlayerSession partyMember : party.getMembers()) {
            visibilityUpdater.update(player, partyMember);
            visibilityUpdater.update(partyMember, player);
        }

        itemBarService.apply(player);
    }
}
