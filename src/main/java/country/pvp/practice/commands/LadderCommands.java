package country.pvp.practice.commands;

import com.google.inject.Inject;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.ladder.LadderManager;
import country.pvp.practice.ladder.LadderRepository;
import country.pvp.practice.util.message.Sender;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Name;
import me.vaperion.blade.command.annotation.Permission;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class LadderCommands {

    private final LadderManager ladderManager;
    private final LadderRepository ladderRepository;

    @Command("ladder create")
    @Permission("practice.admin")
    public void create(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("name") String name) {
        if (ladderManager.get(name) != null) {
            Sender.messageError(sender, "Ladder with this name already exists.");
            return;
        }

        Ladder ladder = new Ladder(name);
        ladderManager.add(ladder);
        ladderRepository.saveAsync(ladder);
        Sender.messageSuccess(sender, ChatColor.GREEN + "Successfully created new ladder.");
    }

    @Command("ladder remove")
    @Permission("practice.admin")
    public void remove(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("ladder") Ladder ladder) {
        ladderManager.remove(ladder);
        ladderRepository.deleteAsync(ladder);
        Sender.messageSuccess(sender, ChatColor.GREEN + "Successfully removed ladder.");
    }

    @Command("ladder displayName")
    @Permission("practice.admin")
    public void displayName(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("ladder") Ladder ladder, @Name("displayName") String name) {
        ladder.setDisplayName(name);
        ladderRepository.saveAsync(ladder);
        Sender.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's display name.");
    }

    @Command("ladder icon")
    @Permission("practice.admin")
    public void icon(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("ladder") Ladder ladder) {
        ItemStack itemInHand = sender.getItemInHand();

        if (itemInHand == null) {
            Sender.messageError(sender, "You must hold an item in your hand.");
            return;
        }

        ladder.setIcon(itemInHand.clone());
        ladderRepository.saveAsync(ladder);
        Sender.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's icon.");
    }

    @Command("ladder ranked")
    @Permission("practice.admin")
    public void ranked(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("ladder") Ladder ladder, @Name("ranked") boolean ranked) {
        ladder.setRanked(ranked);
        ladderRepository.saveAsync(ladder);
        Sender.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's ranked status.");
    }

    @Command("ladder inventory")
    @Permission("practice.admin")
    public void inventory(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("ladder") Ladder ladder) {
        ladder.setInventory(sender.getInventory().getContents());
        ladderRepository.saveAsync(ladder);
        Sender.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's inventory.");
    }

    @Command("ladder armor")
    @Permission("practice.admin")
    public void armor(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("ladder") Ladder ladder) {
        ladder.setArmor(sender.getInventory().getArmorContents());
        ladderRepository.saveAsync(ladder);
        Sender.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's armor.");
    }

    @Command("ladder editoritems")
    @Permission("practice.admin")
    public void editorItems(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("ladder") Ladder ladder) {
        ladder.setEditorItems(sender.getInventory().getContents());
        ladderRepository.saveAsync(ladder);
        Sender.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's editor items.");
    }

    @Command("ladder build")
    @Permission("practice.admin")
    public void build(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("ladder") Ladder ladder, @Name("build") boolean build) {
        ladder.setBuild(build);
        ladderRepository.saveAsync(ladder);
        Sender.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's build.");
    }
}
