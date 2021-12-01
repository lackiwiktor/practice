package country.pvp.practice.queue.command;

import com.google.inject.Inject;
import country.pvp.practice.command.PlayerCommand;
import country.pvp.practice.message.Messager;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.queue.menu.QueueMenuProvider;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Sender;
import org.bukkit.entity.Player;

public class QueueCommand extends PlayerCommand {

    private final QueueMenuProvider queueMenuProvider;

    @Inject
    public QueueCommand(PlayerManager playerManager, QueueMenuProvider queueMenuProvider) {
        super(playerManager);
        this.queueMenuProvider = queueMenuProvider;
    }

    @Command("ranked")
    public void ranked(@Sender Player sender) {
        PracticePlayer senderPlayer = get(sender);

        if (!canJoinQueue(senderPlayer)) {
            Messager.messageError(senderPlayer, "You can join a queue only in the lobby.");
            return;
        }

        queueMenuProvider.provide(true, senderPlayer).openMenu(sender);
    }

    @Command("unranked")
    public void unranked(@Sender Player sender) {
        PracticePlayer senderPlayer = get(sender);

        if (!canJoinQueue(senderPlayer)) {
            Messager.messageError(senderPlayer, "You can join a queue only in the lobby.");
            return;
        }

        queueMenuProvider.provide(false, senderPlayer).openMenu(sender);
    }

    private boolean canJoinQueue(PracticePlayer player) {
        return player.isInLobby();
    }
}
