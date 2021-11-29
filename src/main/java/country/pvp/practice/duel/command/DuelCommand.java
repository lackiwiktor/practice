package country.pvp.practice.duel.command;

import com.google.inject.Inject;
import country.pvp.practice.command.PlayerCommand;
import country.pvp.practice.duel.DuelRequest;
import country.pvp.practice.duel.DuelService;
import country.pvp.practice.message.Messager;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Name;
import me.vaperion.blade.command.annotation.Sender;
import org.bukkit.entity.Player;

public class DuelCommand extends PlayerCommand {

    private final DuelService duelService;

    @Inject
    public DuelCommand(PlayerManager playerManager, DuelService duelService) {
        super(playerManager);
        this.duelService = duelService;
    }

    @Command("duel")
    public void duel(@Sender Player sender, @Name("player") PracticePlayer player) {
        PracticePlayer senderPlayer = get(sender);
        duelService.invite(senderPlayer, player);
    }

    @Command("accept")
    public void accept(@Sender Player sender, @Name("player") PracticePlayer player) {
        PracticePlayer senderPlayer = get(sender);

        if (!senderPlayer.hasDuelRequest(player)) {
            Messager.messageError(sender, "You have not received duel request from this player.");
            return;
        }

        DuelRequest duelRequest = senderPlayer.getDuelRequest(player);
        duelService.acceptDuel(senderPlayer, duelRequest);
    }
}
