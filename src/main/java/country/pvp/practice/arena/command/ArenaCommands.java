package country.pvp.practice.arena.command;

import com.google.inject.Inject;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.arena.ArenaManager;
import country.pvp.practice.arena.ArenaService;
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
public class ArenaCommands {

    private final ArenaManager arenaManager;
    private final ArenaService arenaService;

    @Command("arena create")
    @Permission("practice.admin")
    public void create(@Sender Player sender, @Name("name") String name) {
        if (arenaManager.get(name) != null) {
            Messager.messageError(sender, "Arena with this name already exists.");
            return;
        }

        Arena arena = new Arena(name);
        arenaManager.add(arena);
        arenaService.saveAsync(arena);
        Messager.messageSuccess(sender, ChatColor.GREEN + "Successfully created new arena.");
    }

    @Command("arena remove")
    @Permission("practice.admin")
    public void remove(@Sender Player sender, @Name("arena") Arena arena) {
        arenaManager.remove(arena);
        arenaService.deleteAsync(arena);
        Messager.messageSuccess(sender, ChatColor.GREEN + "Successfully removed arena.");
    }

    @Command("arena displayName")
    @Permission("practice.admin")
    public void displayName(@Sender Player sender, @Name("arena") Arena arena, @Name("displayName") String name) {
        arena.setDisplayName(name);
        arenaService.saveAsync(arena);
        Messager.messageSuccess(sender, ChatColor.GREEN + "Successfully set arena's display name.");
    }

    @Command("arena icon")
    @Permission("practice.admin")
    public void icon(@Sender Player sender, @Name("arena") Arena arena) {
        ItemStack itemInHand = sender.getItemInHand();

        if (itemInHand == null) {
            Messager.messageError(sender, "You must hold an item in your hand.");
            return;
        }

        arena.setIcon(itemInHand.clone());
        arenaService.saveAsync(arena);
        Messager.messageSuccess(sender, ChatColor.GREEN + "Successfully set arena's icon.");
    }

    @Command("arena spawnLocation1")
    @Permission("practice.admin")
    public void spawnLocation1(@Sender Player sender, @Name("arena") Arena arena) {
        arena.setSpawnLocation1(sender.getLocation().getBlock().getLocation());
        arenaService.saveAsync(arena);
        Messager.messageSuccess(sender, ChatColor.GREEN + "Successfully set arena's first spawn location.");
    }

    @Command("arena spawnLocation2")
    @Permission("practice.admin")
    public void spawnLocation2(@Sender Player sender, @Name("arena") Arena arena) {
        arena.setSpawnLocation2(sender.getLocation().getBlock().getLocation());
        arenaService.saveAsync(arena);
        Messager.messageSuccess(sender, ChatColor.GREEN + "Successfully set arena's second spawn location.");
    }

    @Command("arena center")
    @Permission("practice.admin")
    public void center(@Sender Player sender, @Name("arena") Arena arena) {
        arena.setCenter(sender.getLocation().getBlock().getLocation());
        arenaService.saveAsync(arena);
        Messager.messageSuccess(sender, ChatColor.GREEN + "Successfully set arena's center location.");
    }

    @Command("arena spectatorLocation")
    @Permission("practice.admin")
    public void spectatorLocation(@Sender Player sender, @Name("arena") Arena arena) {
        arena.setSpectatorLocation(sender.getLocation().getBlock().getLocation());
        arenaService.saveAsync(arena);
        Messager.messageSuccess(sender, ChatColor.GREEN + "Successfully set arena's spectator location.");
    }
}
