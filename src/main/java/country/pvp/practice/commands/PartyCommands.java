package country.pvp.practice.commands;

import com.google.inject.Inject;
import country.pvp.practice.message.Messager;
import country.pvp.practice.party.Party;
import country.pvp.practice.party.PartyManager;
import country.pvp.practice.party.PartyService;
import country.pvp.practice.party.menu.PartyEventMenuProvider;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Name;
import me.vaperion.blade.command.annotation.Optional;
import me.vaperion.blade.command.annotation.Sender;
import org.bukkit.entity.Player;

public class PartyCommands extends PlayerCommands {

    private final PartyService partyService;
    private final PartyManager partyManager;
    private final PartyEventMenuProvider partyEventMenuProvider;

    @Inject
    public PartyCommands(PlayerManager playerManager, PartyService partyService, PartyManager partyManager, PartyEventMenuProvider partyEventMenuProvider) {
        super(playerManager);
        this.partyService = partyService;
        this.partyManager = partyManager;
        this.partyEventMenuProvider = partyEventMenuProvider;
    }

    @Command("party create")
    public void create(@Sender Player sender) {
        PlayerSession leader = get(sender);
        partyService.createParty(leader);
    }

    @Command("party disband")
    public void disband(@Sender Player sender) {
        PlayerSession leader = get(sender);

        if (!leader.isInLobby()) {
            Messager.messageError(leader, "You must be in the lobby in order to disband your party.");
            return;
        }

        if (!leader.hasParty()) {
            Messager.messageError(leader, "You do not have a party.");
            return;
        }

        Party party = leader.getParty();
        partyService.disbandParty(leader, party);
    }

    @Command("party invite")
    public void invite(@Sender Player sender, @Name("player") PlayerSession invitee) {
        PlayerSession inviter = get(sender);

        if (!inviter.hasParty()) {
            Messager.messageError(inviter, "You do not have a party.");
            return;
        }

        if (inviter.equals(invitee)) {
            Messager.messageError(inviter, "You can't invite yourself to a party.");
            return;
        }

        if (invitee.hasParty()) {
            Messager.messageError(inviter, "This player already has a party.");
            return;
        }

        Party party = inviter.getParty();

        if (party.isPlayerInvited(invitee)) {
            Messager.messageError(inviter, "This player has already been invited to the party.");
            return;
        }

        partyService.inviteToParty(inviter, invitee, party);
    }

    @Command("party kick")
    public void kick(@Sender Player sender, @Name("player") PlayerSession member) {
        PlayerSession leader = get(sender);

        if (leader.equals(member)) {
            Messager.messageError(leader, "You can't kick yourself from a party, if you wish to disband it use /party disband.");
            return;
        }

        if (!leader.hasParty()) {
            Messager.messageError(leader, "You do not have a party.");
            return;
        }

        Party party = leader.getParty();
        partyService.kickFromParty(party, member);
    }

    @Command("party info")
    public void info(@Sender Player sender, @Optional @Name("player") PlayerSession player) {
        if (player != null) {
            if (!player.hasParty()) {
                Messager.messageError(sender, "This player is not in a party.");
                return;
            }
        } else {
            PlayerSession senderPlayer = get(sender);

            if (!senderPlayer.hasParty()) {
                Messager.messageError(sender, "You are not in a party.");
                return;
            }

            player = senderPlayer;
        }

        Party party = player.getParty();
        Messager.message(sender, "Party Members: " + party.getMembers().size());
    }

    @Command("party event")
    public void info(@Sender Player sender) {
        PlayerSession leader = get(sender);

        if (!leader.hasParty()) {
            Messager.messageError(leader, "You do not have a party.");
            return;
        }

        Party party = leader.getParty();

        if (!party.isLeader(leader)) {
            Messager.messageError(leader, "You must be a leader of the party to start party event.");
            return;
        }

        partyEventMenuProvider.provide(party).openMenu(sender);
    }

    @Command("party list")
    public void list(@Sender Player sender) {
        for (Party party : partyManager.getAll()) {
            sender.sendMessage(getShortPartyInfo(party));
        }
    }

    private String getShortPartyInfo(Party party) {
        return party.getName() + " (" + party.getMembers().size() + "/20";
    }
}
