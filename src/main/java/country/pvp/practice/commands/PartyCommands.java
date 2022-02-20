package country.pvp.practice.commands;

import com.google.inject.Inject;
import country.pvp.practice.Messages;
import country.pvp.practice.kit.editor.KitChooseMenuProvider;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.party.Party;
import country.pvp.practice.party.PartyManager;
import country.pvp.practice.party.PartyService;
import country.pvp.practice.party.duel.PartyDuelService;
import country.pvp.practice.party.menu.PartyEventMenuProvider;
import country.pvp.practice.party.menu.PartyMembersMenuProvider;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.util.message.MessagePattern;
import country.pvp.practice.util.message.Sender;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Name;
import me.vaperion.blade.command.annotation.Optional;
import org.bukkit.entity.Player;

public class PartyCommands extends PlayerCommands {

    private final PartyService partyService;
    private final PartyManager partyManager;
    private final PartyDuelService partyDuelService;
    private final PartyEventMenuProvider partyEventMenuProvider;
    private final PartyMembersMenuProvider partyMembersMenuProvider;
    private final KitChooseMenuProvider kitChooseMenuProvider;

    @Inject
    public PartyCommands(PlayerManager playerManager, PartyService partyService, PartyManager partyManager, PartyDuelService partyDuelService, PartyEventMenuProvider partyEventMenuProvider, PartyMembersMenuProvider partyMembersMenuProvider, KitChooseMenuProvider kitChooseMenuProvider) {
        super(playerManager);
        this.partyService = partyService;
        this.partyManager = partyManager;
        this.partyDuelService = partyDuelService;
        this.partyEventMenuProvider = partyEventMenuProvider;
        this.partyMembersMenuProvider = partyMembersMenuProvider;
        this.kitChooseMenuProvider = kitChooseMenuProvider;
    }

    @Command("party create")
    public void create(@me.vaperion.blade.command.annotation.Sender Player sender) {
        PlayerSession leader = get(sender);
        partyService.createParty(leader);
    }

    @Command("party leave")
    public void leave(@me.vaperion.blade.command.annotation.Sender Player sender) {
        PlayerSession playerSession = get(sender);

        if (!playerSession.hasParty()) {
            Sender.messageError(playerSession, "You do not have a party.");
            return;
        }

        Party party = playerSession.getParty();
        partyService.leaveFromParty(party, playerSession);
    }

    @Command("party disband")
    public void disband(@me.vaperion.blade.command.annotation.Sender Player sender) {
        PlayerSession leader = get(sender);

        if (!leader.hasParty()) {
            Sender.messageError(leader, "You do not have a party.");
            return;
        }

        if (!leader.isPartyLeader()) {
            Sender.messageError(leader, "You must be the leader of the party in order to disband it.");
            return;
        }

        Party party = leader.getParty();
        partyService.disbandParty(leader, party);
    }

    @Command("party invite")
    public void invite(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("player") PlayerSession invitee) {
        PlayerSession inviter = get(sender);

        if (!inviter.hasParty()) {
            Sender.messageError(inviter, "You do not have a party.");
            return;
        }

        if (inviter.equals(invitee)) {
            Sender.messageError(inviter, "You can't invite yourself to a party.");
            return;
        }

        if (invitee.hasParty()) {
            Sender.messageError(inviter, "This player already has a party.");
            return;
        }

        Party party = inviter.getParty();

        if (party.isPlayerInvited(invitee)) {
            Sender.messageError(inviter, "This player has already been invited to the party.");
            return;
        }

        partyService.inviteToParty(inviter, invitee, party);
    }

    @Command("party kick")
    public void kick(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("player") PlayerSession member) {
        PlayerSession leader = get(sender);

        if (leader.equals(member)) {
            Sender.messageError(leader, "You can't kick yourself from a party, if you wish to disband it use /party disband.");
            return;
        }

        if (!leader.hasParty()) {
            Sender.messageError(leader, "You do not have a party.");
            return;
        }

        Party party = leader.getParty();
        partyService.kickFromParty(party, member);
    }

    @Command("party info")
    public void info(@me.vaperion.blade.command.annotation.Sender Player sender, @Optional @Name("player") PlayerSession player) {
        if (player != null) {
            if (!player.hasParty()) {
                Sender.messageError(sender, "This player is not in a party.");
                return;
            }
        } else {
            PlayerSession senderPlayer = get(sender);

            if (!senderPlayer.hasParty()) {
                Sender.messageError(sender, "You are not in a party.");
                return;
            }

            player = senderPlayer;
        }

        Party party = player.getParty();
        Sender.message(sender, "Party Members: " + party.getMembers().size());
    }

    @Command("party event")
    public void info(@me.vaperion.blade.command.annotation.Sender Player sender) {
        PlayerSession leader = get(sender);

        if (!leader.hasParty()) {
            Sender.messageError(leader, "You do not have a party.");
            return;
        }

        Party party = leader.getParty();

        if (!party.isLeader(leader)) {
            Sender.messageError(leader, "You must be a leader of the party to start party event.");
            return;
        }

        if (!party.isInLobby()) {
            Sender.messageError(leader, "You must be in lobby in order to start a party event");
            return;
        }

        partyEventMenuProvider.provide(party).openMenu(sender);
    }

    @Command("party members")
    public void members(@me.vaperion.blade.command.annotation.Sender Player sender) {
        PlayerSession inviter = get(sender);

        if (!inviter.hasParty()) {
            Sender.messageError(inviter, "You do not have a party.");
            return;
        }

        if (!inviter.isInLobby()) {
            Sender.messageError(inviter, "You must be in the lobby in order to view party members.");
            return;
        }

        partyMembersMenuProvider.provide(inviter.getParty()).openMenu(sender);
    }

    @Command("party list")
    public void list(@me.vaperion.blade.command.annotation.Sender Player sender) {
        for (Party party : partyManager.getAll()) {
            sender.sendMessage(getShortPartyInfo(party));
        }
    }

    @Command("party duel")
    public void duel(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("party") Party invitee, @Optional @Name("ladder") Ladder ladder) {
        PlayerSession leader = get(sender);
        if (!leader.hasParty()) {
            Sender.messageError(leader, "You do not have a party.");
            return;
        }

        if (!leader.isPartyLeader()) {
            Sender.messageError(leader, "You must be a leader of the party to start party event.");
            return;
        }

        Party inviter = leader.getParty();

        if (ladder != null) {
            partyDuelService.invite(inviter, invitee, ladder, Messages.PARTY_DUEL_INVITATION.match(
                    new MessagePattern("{party}", inviter.getName()),
                    new MessagePattern("{ladder}", ladder.getDisplayName())));
        } else {
            kitChooseMenuProvider
                    .provide((l) -> partyDuelService.invite(inviter, invitee, l, Messages.PARTY_DUEL_INVITATION.match(
                            new MessagePattern("{party}", inviter.getName()),
                            new MessagePattern("{ladder}", l.getDisplayName()))))
                    .openMenu(sender);
        }
    }

    private String getShortPartyInfo(Party party) {
        return party.getName() + " (" + party.getMembers().size() + "/20";
    }
}
