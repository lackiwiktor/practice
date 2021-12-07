package country.pvp.practice.commands;

import com.google.inject.Inject;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.ladder.LadderManager;
import country.pvp.practice.ladder.LadderService;
import country.pvp.practice.message.Messager;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Name;
import me.vaperion.blade.command.annotation.Permission;
import me.vaperion.blade.command.annotation.Sender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class LadderCommands {

    private final LadderManager ladderManager;
    private final LadderService ladderService;

    @Command("ladder create")
    @Permission("practice.admin")
    public void create(@Sender Player sender, @Name("name") String name) {
        if (ladderManager.get(name) != null) {
            Messager.messageError(sender, "Ladder with this name already exists.");
            return;
        }

        Ladder ladder = new Ladder(name);
        ladderManager.add(ladder);
        ladderService.saveAsync(ladder);
        Messager.messageSuccess(sender, ChatColor.GREEN + "Successfully created new ladder.");
    }

    @Command("ladder remove")
    @Permission("practice.admin")
    public void remove(@Sender Player sender, @Name("ladder") Ladder ladder) {
        ladderManager.remove(ladder);
        ladderService.deleteAsync(ladder);
        Messager.messageSuccess(sender, ChatColor.GREEN + "Successfully removed ladder.");
    }

    @Command("ladder displayName")
    @Permission("practice.admin")
    public void displayName(@Sender Player sender, @Name("ladder") Ladder ladder, @Name("displayName") String name) {
        ladder.setDisplayName(name);
        ladderService.saveAsync(ladder);
        Messager.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's display name.");
    }

    @Command("ladder icon")
    @Permission("practice.admin")
    public void icon(@Sender Player sender, @Name("ladder") Ladder ladder) {
        ItemStack itemInHand = sender.getItemInHand();

        if (itemInHand == null) {
            Messager.messageError(sender, "You must hold an item in your hand.");
            return;
        }

        ladder.setIcon(itemInHand.clone());
        ladderService.saveAsync(ladder);
        Messager.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's icon.");
    }

    @Command("ladder ranked")
    @Permission("practice.admin")
    public void ranked(@Sender Player sender, @Name("ladder") Ladder ladder, @Name("ranked") boolean ranked) {
        ladder.setRanked(ranked);
        ladderService.saveAsync(ladder);
        Messager.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's ranked status.");
    }

    @Command("ladder inventory")
    @Permission("practice.admin")
    public void inventory(@Sender Player sender, @Name("ladder") Ladder ladder) {
        ladder.setInventory(sender.getInventory().getContents());
        ladderService.saveAsync(ladder);
        Messager.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's inventory.");
    }

    @Command("ladder armor")
    @Permission("practice.admin")
    public void armor(@Sender Player sender, @Name("ladder") Ladder ladder) {
        ladder.setArmor(sender.getInventory().getArmorContents());
        ladderService.saveAsync(ladder);
        Messager.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's armor.");
    }

    @Command("ladder editoritems")
    @Permission("practice.admin")
    public void editorItems(@Sender Player sender, @Name("ladder") Ladder ladder) {
        ladder.setEditorItems(sender.getInventory().getContents());
        ladderService.saveAsync(ladder);
        Messager.messageSuccess(sender, ChatColor.GREEN + "Successfully set ladder's editor items.");
    }
}
