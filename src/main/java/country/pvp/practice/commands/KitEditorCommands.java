package country.pvp.practice.commands;

import com.google.inject.Inject;
import country.pvp.practice.kit.editor.KitChooseMenuProvider;
import country.pvp.practice.kit.editor.KitEditorService;
import country.pvp.practice.util.message.Sender;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import me.vaperion.blade.command.annotation.Command;
import org.bukkit.entity.Player;

public class KitEditorCommands extends PlayerCommands {

    private final KitEditorService kitEditorService;
    private final KitChooseMenuProvider kitChooseMenuProvider;

    @Inject
    public KitEditorCommands(PlayerManager playerManager, KitEditorService kitEditorService, KitChooseMenuProvider kitChooseMenuProvider) {
        super(playerManager);
        this.kitEditorService = kitEditorService;
        this.kitChooseMenuProvider = kitChooseMenuProvider;
    }

    @Command("kiteditor")
    public void kitEditor(@me.vaperion.blade.command.annotation.Sender Player sender) {
        PlayerSession senderPlayer = get(sender);

        if (!senderPlayer.isInLobby()) {
            Sender.messageError(senderPlayer, "You can enter the kit editor only in the lobby.");
            return;
        }

        kitChooseMenuProvider.provide((ladder) -> kitEditorService.moveToEditor(senderPlayer, ladder)).openMenu(sender);
    }
}
