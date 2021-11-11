package country.pvp.practice.settings;

import com.google.inject.Inject;
import country.pvp.practice.message.Messager;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Permission;
import me.vaperion.blade.command.annotation.Sender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PracticeSettingsCommand {

    private final PracticeSettings practiceSettings;
    private final PracticeSettingsService practiceSettingsService;

    @Command(value = "setspawn", description = "Sets the spawn location")
    @Permission("practice.admin")
    public void setSpawn(@Sender Player sender) {
        practiceSettings.setSpawnLocation(sender.getLocation().getBlock().getLocation());
        practiceSettingsService.saveAsync(practiceSettings);
        Messager.messageSuccess(sender, "Successfully set the spawn location.");
    }
}
