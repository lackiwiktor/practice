package country.pvp.practice.party;

import com.google.common.collect.Sets;
import country.pvp.practice.duel.Request;
import country.pvp.practice.message.Messager;
import country.pvp.practice.message.Recipient;
import country.pvp.practice.player.PlayerSession;
import lombok.Data;
import org.bukkit.ChatColor;

import java.util.Objects;
import java.util.Set;

@Data
public class Party implements Recipient {

    private PlayerSession leader;
    private final Set<PlayerSession> members = Sets.newHashSet();
    private final Set<PartyInviteRequest> requests = Sets.newConcurrentHashSet();
    private boolean disbanded;

    public Party(PlayerSession leader) {
        this.leader = leader;
    }

    public void addPlayer(PlayerSession player) {
        broadcast("Player " + player.getName() + " has joined the party.");
        members.add(player);
    }

    public void removePlayer(PlayerSession player) {
        members.remove(player);
        broadcast("Player " + player.getName() + " has left the party.");
    }

    public boolean hasPlayer(PlayerSession player) {
        return members.contains(player);
    }

    public void invite(PlayerSession invitee) {
        requests.add(new PartyInviteRequest(invitee));
    }

    public boolean isPlayerInvited(PlayerSession invitee) {
        return requests.stream().anyMatch(it -> it.getInvitee().equals(invitee));
    }

    public void disband() {
        disbanded = true;
    }

    public void invalidateInviteRequests() {
        requests.removeIf(Request::hasExpired);
    }

    public String getName() {
        return leader.getName();
    }

    public int size() {
        return members.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Party party = (Party) o;
        return Objects.equals(leader, party.leader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leader);
    }

    public void removePlayerInvite(PlayerSession invitee) {
        requests.removeIf(it -> it.getInvitee().equals(invitee));
    }

    public void handleDisconnect(PlayerSession member) {
        removePlayer(member);
        broadcast(ChatColor.BLUE + "Player " + member.getName() + " has disconnected.");
    }

    private void broadcast(String message) {
        Messager.message(this, message);
    }

    @Override
    public void receive(String message) {
        members.forEach(it -> Messager.message(it, message));
    }
}
