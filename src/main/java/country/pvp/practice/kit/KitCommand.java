package country.pvp.practice.kit;

import com.google.inject.Inject;
import country.pvp.practice.kit.editor.KitEditorMenu;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.PlayerManager;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Name;
import me.vaperion.blade.command.annotation.Sender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class KitCommand {

    private final PlayerManager playerManager;

    @Command("kiteditor")
    public void execute(@Sender Player player, @Name("ladder") Ladder ladder) {
        new KitEditorMenu(playerManager.get(player), ladder).openMenu(player);
    }
}
