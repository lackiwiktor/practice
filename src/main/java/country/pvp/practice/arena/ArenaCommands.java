package country.pvp.practice.arena;

import com.google.inject.Inject;
import country.pvp.practice.PracticePlugin;
import country.pvp.practice.commands.PlayerCommands;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.player.data.PlayerState;
import country.pvp.practice.util.Region;
import country.pvp.practice.util.WorldEditUtils;
import country.pvp.practice.util.message.Sender;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Name;
import me.vaperion.blade.command.annotation.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Set;


public class ArenaCommands extends PlayerCommands {

    private final ArenaManager arenaManager;
    private final ArenaService arenaService;
    private final DuplicatedArenaService duplicatedArenaService;
    private final DuplicatedArenaManager duplicatedArenaManager;

    public static final File WORLD_EDIT_SCHEMATICS_FOLDER = new File(JavaPlugin.getPlugin(PracticePlugin.class).getDataFolder(), "schematics");

    @Inject
    public ArenaCommands(PlayerManager playerManager, ArenaManager arenaManager, ArenaService arenaService, DuplicatedArenaService duplicatedArenaService, DuplicatedArenaManager duplicatedArenaManager) {
        super(playerManager);
        this.arenaManager = arenaManager;
        this.arenaService = arenaService;
        this.duplicatedArenaService = duplicatedArenaService;
        this.duplicatedArenaManager = duplicatedArenaManager;
    }

    @Command("arena create")
    @Permission("practice.admin")
    public void create(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("name") String name) {
        if (arenaManager.get(name) != null) {
            Sender.messageError(sender, "Arena with this name already exists.");
            return;
        }

        Arena arena = new Arena(name);
        arenaManager.add(arena);
        arenaService.saveAsync(arena);
        Sender.messageSuccess(sender, ChatColor.GREEN + "Successfully created new arena.");
    }

    @Command("arena remove")
    @Permission("practice.admin")
    public void remove(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("arena") Arena arena) {
        arenaManager.remove(arena);
        arenaService.deleteAsync(arena);
        Sender.messageSuccess(sender, ChatColor.GREEN + "Successfully removed arena.");
    }

    @Command("arena displayName")
    @Permission("practice.admin")
    public void displayName(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("arena") Arena arena, @Name("displayName") String name) {
        arena.setDisplayName(name);
        arenaService.saveAsync(arena);
        Sender.messageSuccess(sender, ChatColor.GREEN + "Successfully set arena's display name.");
    }

    @Command("arena icon")
    @Permission("practice.admin")
    public void icon(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("arena") Arena arena) {
        ItemStack itemInHand = sender.getItemInHand();

        if (itemInHand == null) {
            Sender.messageError(sender, "You must hold an item in your hand.");
            return;
        }

        arena.setIcon(itemInHand.clone());
        arenaService.saveAsync(arena);
        Sender.messageSuccess(sender, ChatColor.GREEN + "Successfully set arena's icon.");
    }

    @Command("arena selector")
    @Permission("practice.admin")
    public void selector(@me.vaperion.blade.command.annotation.Sender Player sender) {
        PlayerSession session = get(sender);
        session.setState(PlayerState.SELECTING, new SessionSelectionData());
        sender.getInventory().addItem(new ItemStack(Material.GOLD_PICKAXE));
    }

    @Command("arena gridindex")
    public void gridIndex(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("arena") Arena arena, @Name("index") int index) {
        arena.setGridIndex(index);
        arenaService.saveAsync(arena);
        Sender.messageSuccess(sender, "Successfully set grid index.");
    }

    @Command("arena region")
    public void region(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("arena") Arena arena) {
        PlayerSession session = get(sender);

        if (!session.isSelecting()) {
            Sender.messageError(sender, "You must be in editing mode to set region.");
            return;
        }

        SessionSelectionData selectionData = session.getStateData();

        if (!selectionData.isReady()) {
            Sender.messageSuccess(session, "You haven't selected region yet!");
            return;
        }

        Region region = Region.from(selectionData.getSelection());
        arena.setRegion(region);
        arenaService.saveAsync(arena);


        File file = new File(WORLD_EDIT_SCHEMATICS_FOLDER, arena.getName() + ".schematic");

        try {
            WorldEditUtils.save(file, sender.getWorld(), region);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Sender.messageSuccess(sender, "Success!");
        }
    }

    @Command("arena generate")
    public void generate(@me.vaperion.blade.command.annotation.Sender Player sender, @Name("arena") Arena arena, @Name("amount") int amount) {
        if (arena.getSchematic() == null) {
            Sender.messageError(sender, "Schematic is null");
            return;
        }

        try {
            Set<DuplicatedArena> arenas = ArenaGenerator.generate(arena, amount);
            duplicatedArenaManager.add(arena, arenas);
            arenas.forEach(it -> duplicatedArenaService.saveAsync(it));
            System.out.println("Success, I guess???");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Command("arena teleport")
    public void teleport(@me.vaperion.blade.command.annotation.Sender Player sender) {
        sender.teleport(new Location(Bukkit.getWorld("arenas"), 1_000, 60, 1_000));
    }
}
