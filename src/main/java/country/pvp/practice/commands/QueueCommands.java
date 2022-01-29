package country.pvp.practice.commands;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.queue.menu.QueueMenuProvider;
import country.pvp.practice.util.message.Sender;
import me.vaperion.blade.command.annotation.Command;
import org.bukkit.entity.Player;

public class QueueCommands extends PlayerCommands {

    private final QueueMenuProvider queueMenuProvider;

    @Inject
    public QueueCommands(PlayerManager playerManager, QueueMenuProvider queueMenuProvider) {
        super(playerManager);
        this.queueMenuProvider = queueMenuProvider;
    }

    @Command(value = "ranked", async = true)
    public void ranked(@me.vaperion.blade.command.annotation.Sender Player sender) {
        PlayerSession senderPlayer = get(sender);

        if (!canJoinQueue(senderPlayer)) {
            return;
        }

        queueMenuProvider.provide(true, senderPlayer).openMenu(sender);
    }

    @Command(value = "unranked", async = true)
    public void unranked(@me.vaperion.blade.command.annotation.Sender Player sender) {
        PlayerSession senderPlayer = get(sender);

        if(!canJoinQueue(senderPlayer)) {
            return;
        }

        queueMenuProvider.provide(false, senderPlayer).openMenu(sender);
    }

    private boolean canJoinQueue(PlayerSession player) {
        if (!player.isInLobby()) {
            Sender.messageError(player, "You can join a queue only in the lobby.");
            return false;
        }

        if (player.hasParty()) {
            Sender.messageError(player, "You can join a queue while being in a party.");
            return false;
        }

        return true;
    }
}
