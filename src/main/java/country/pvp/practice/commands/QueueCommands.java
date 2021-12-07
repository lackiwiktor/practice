package country.pvp.practice.commands;

import com.google.inject.Inject;
import country.pvp.practice.message.Messager;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.queue.menu.QueueMenuProvider;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Sender;
import org.bukkit.entity.Player;

public class QueueCommands extends PlayerCommands {

    private final QueueMenuProvider queueMenuProvider;

    @Inject
    public QueueCommands(PlayerManager playerManager, QueueMenuProvider queueMenuProvider) {
        super(playerManager);
        this.queueMenuProvider = queueMenuProvider;
    }

    @Command("ranked")
    public void ranked(@Sender Player sender) {
        PlayerSession senderPlayer = get(sender);

        if (!canJoinQueue(senderPlayer)) {
            Messager.messageError(senderPlayer, "You can join a queue only in the lobby.");
            return;
        }

        queueMenuProvider.provide(true, senderPlayer).openMenu(sender);
    }

    @Command("unranked")
    public void unranked(@Sender Player sender) {
        PlayerSession senderPlayer = get(sender);

        if (!canJoinQueue(senderPlayer)) {
            Messager.messageError(senderPlayer, "You can join a queue only in the lobby.");
            return;
        }

        queueMenuProvider.provide(false, senderPlayer).openMenu(sender);
    }

    private boolean canJoinQueue(PlayerSession player) {
        return player.isInLobby();
    }
}
