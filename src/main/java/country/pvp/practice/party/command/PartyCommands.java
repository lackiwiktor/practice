package country.pvp.practice.party.command;

import com.google.inject.Inject;
import country.pvp.practice.command.PlayerCommand;
import country.pvp.practice.message.Messager;
import country.pvp.practice.party.Party;
import country.pvp.practice.party.PartyManager;
import country.pvp.practice.party.PartyService;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Name;
import me.vaperion.blade.command.annotation.Optional;
import me.vaperion.blade.command.annotation.Sender;
import org.bukkit.entity.Player;

public class PartyCommands extends PlayerCommand {

    private final PartyService partyService;
    private final PartyManager partyManager;

    @Inject
    public PartyCommands(PlayerManager playerManager, PartyService partyService, PartyManager partyManager) {
        super(playerManager);
        this.partyService = partyService;
        this.partyManager = partyManager;
    }

    @Command("party create")
    public void create(@Sender Player sender) {
        PlayerSession senderPlayer = get(sender);

        partyService.createParty(senderPlayer);
    }

    @Command("party disband")
    public void disband(@Sender Player sender) {
        PlayerSession senderPlayer = get(sender);

        if (!senderPlayer.isInLobby()) {
            Messager.messageError(senderPlayer, "You must be in the lobby in order to disband your party.");
            return;
        }

        if (!senderPlayer.hasParty()) {
            Messager.messageError(senderPlayer, "You do not have a party.");
            return;
        }

        partyService.disbandParty(senderPlayer, senderPlayer.getParty());
    }

    @Command("party invite")
    public void invite(@Sender Player sender, @Name("player") PlayerSession invitee) {
        PlayerSession inviter = get(sender);

        if (inviter.equals(invitee)) {
            Messager.messageError(inviter, "You can't invite yourself to a party.");
            return;
        }

        if (!inviter.isInLobby()) {
            Messager.messageError(inviter, "You must be in the lobby in order to invite player to a party.");
            return;
        }

        if (!inviter.hasParty()) {
            Messager.messageError(inviter, "You do not have a party.");
            return;
        }

        Party party = inviter.getParty();
        partyService.inviteToParty(inviter, invitee, party);
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
