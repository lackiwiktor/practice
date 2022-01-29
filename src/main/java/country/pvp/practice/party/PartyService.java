package country.pvp.practice.party;

import com.google.inject.Inject;
import country.pvp.practice.invitation.Invitation;
import country.pvp.practice.invitation.InvitationService;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.util.message.Sender;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PartyService {

    private final VisibilityUpdater visibilityUpdater;
    private final ItemBarService itemBarService;
    private final InvitationService invitationService;
    private final PartyManager partyManager;
    private final MatchManager matchManager;

    public void createParty(PlayerSession leader) {
        if (leader.hasParty()) {
            Sender.messageError(leader, "You already are in a party.");
            return;
        }

        Party party = new Party(leader);
        partyManager.add(party);
        check(party, leader);
    }

    public void disbandParty(PlayerSession senderPlayer, Party party) {
        if (!senderPlayer.isPartyLeader()) {
            Sender.messageError(senderPlayer, "You are not the leader of the party.");
            return;
        }

        for (PlayerSession player : party.getMembers()) {
            player.removeFromParty();
        }

        party.disband();

        if (party.isInLobby()) {
            for (PlayerSession player : party.getMembers()) {
                itemBarService.apply(player);
                for (PlayerSession other : party.getMembers()) {
                    visibilityUpdater.update(player, other);
                    visibilityUpdater.update(other, player);
                }
            }
        }

        partyManager.remove(party);
    }

    public void inviteToParty(PlayerSession inviter, PlayerSession invitee, Party party) {
        Invitation invitation = new Invitation("You have been invited to " + party.getName() + " party.", invitee) {
            @Override
            protected boolean onAccept() {
                return acceptInvite(party, invitee);
            }

            @Override
            protected void onDecline() {
                party.removePlayerInvite(invitee);
            }
        };

        party.invite(invitee);
        invitationService.invite(invitee, invitation);
    }

    public boolean acceptInvite(Party party, PlayerSession player) {
        return addToParty(party, player);
    }

    private boolean check(Party party, PlayerSession player) {
        if (!player.isInLobby()) {
            Sender.messageError(player, "You must be in the lobby in order to accept a party invite");
            return false;
        }

        if (party.isDisbanded()) {
            Sender.messageError(player, "This party has been disbanded.");
            return true;
        }

        if (player.hasParty()) {
            Sender.messageError(player, "You already are in a party.");
            return false;
        }

        if (!party.isInLobby()) {
            Sender.messageError(player, "Party must be in lobby in order to join it.");
            return false;
        }

        party.addPlayer(player);
        player.addToParty(party);
        update(player, party);
        return true;
    }

    private boolean addToParty(Party party, PlayerSession player) {
        if (!party.isPlayerInvited(player)) {
            Sender.messageError(player, "You have not been invited to this party or the request has expired.");
            return false;
        }

        return check(party, player);
    }

    public void kickFromParty(Party party, PlayerSession session) {
        party.removePlayer(session, Party.RemoveReason.KICKED);
        session.removeFromParty();

        if (session.isInLobby()) {
            update(session, party);
            itemBarService.apply(session);
        }
    }

    public void leaveFromParty(Party party, PlayerSession session) {
        if (session.isPartyLeader()) {
            if (party.size() == 1) {
                disbandParty(session, party);
                return;
            }

            List<PlayerSession> membersLeft = new ArrayList<>(party.getMembers());
            membersLeft.remove(session);
            int random = ThreadLocalRandom.current().nextInt(0, membersLeft.size());
            PlayerSession newLeader = membersLeft.stream().toArray(PlayerSession[]::new)[random];
            party.setLeader(newLeader);
            Sender.message(newLeader, "You are now the new leader of the party.");

            if(newLeader.isInLobby()) {
                itemBarService.apply(newLeader);
            }
        }

        party.removePlayer(session, Party.RemoveReason.LEFT);
        session.removeFromParty();

        if (session.isInLobby()) {
            update(session, party);
            itemBarService.apply(session);
        }
    }

    private void update(PlayerSession player, Party party) {
        for (PlayerSession partyMember : party.getMembers()) {
            visibilityUpdater.update(player, partyMember);
            visibilityUpdater.update(partyMember, player);
        }

        itemBarService.apply(player);
    }
}
