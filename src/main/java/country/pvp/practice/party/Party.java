package country.pvp.practice.party;

import com.google.common.collect.Sets;
import country.pvp.practice.Messages;
import country.pvp.practice.duel.DuelInvitable;
import country.pvp.practice.duel.Request;
import country.pvp.practice.party.duel.PartyDuelRequest;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.util.message.Recipient;
import country.pvp.practice.util.message.Sender;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Data
public class Party implements DuelInvitable<Party, PartyDuelRequest>, Recipient {

    private final UUID id = UUID.randomUUID(); //party-id
    private final Set<PlayerSession> members = Sets.newHashSet();
    private final Set<PartyDuelRequest> duelRequests = Sets.newConcurrentHashSet();
    private final Set<PartyInviteRequest> requests = Sets.newConcurrentHashSet();
    private PlayerSession leader;
    private boolean disbanded;

    public Party(PlayerSession leader) {
        this.leader = leader;
    }

    public boolean isLeader(PlayerSession session) {
        return leader.equals(session);
    }

    public void addPlayer(PlayerSession player) {
        broadcast(Messages.PARTY_PLAYER_JOINED.match("{player}", player.getName()));
        members.add(player);
    }

    public void removePlayer(PlayerSession player, RemoveReason reason) {
        members.remove(player);
        player.removeFromParty();
        broadcast(reason.message.match("{player}", player.getName()));
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

    public void removePlayerInvite(PlayerSession invitee) {
        requests.removeIf(it -> it.getInvitee().equals(invitee));
    }

    public void handleDisconnect(PlayerSession member) {
        removePlayer(member, RemoveReason.DISCONNECTED);
        broadcast(ChatColor.BLUE + "Player " + member.getName() + " has disconnected.");
    }

    private void broadcast(String message) {
        members.forEach(it -> Sender.message(it, message));
    }

    @Override
    public void receive(String message) {
        leader.receive(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Party party = (Party) o;
        return Objects.equals(id, party.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public void addDuelRequest(PartyDuelRequest request) {
        duelRequests.add(request);
    }

    @Override
    public boolean hasDuelRequest(DuelInvitable inviter) {
        return duelRequests.stream().anyMatch(it -> it.getInviter().equals(inviter));
    }

    @Override
    public void clearDuelRequests(DuelInvitable inviter) {
        duelRequests.removeIf(it -> it.getInviter().equals(inviter));
    }

    @Override
    public void invalidateDuelRequests() {
        duelRequests.removeIf(Request::hasExpired);
    }

    @Override
    public @Nullable PartyDuelRequest getDuelRequest(DuelInvitable inviter) {
        return duelRequests.stream()
                .filter(it -> it.getInviter().equals(inviter))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void receiveInvite(BaseComponent[] component) {
        leader.sendComponent(component);
    }


    public boolean isInLobby() {
        return members.stream().noneMatch(it -> it.isInMatch());
    }

    @RequiredArgsConstructor
    @Getter
    public enum RemoveReason {

        LEFT(Messages.PARTY_PLAYER_LEFT),
        KICKED(Messages.PARTY_PLAYER_WAS_KICKED),
        DISCONNECTED(Messages.PARTY_PLAYER_DISCONNECTED);

        private final Messages message;
    }
}
