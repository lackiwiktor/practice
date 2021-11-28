package country.pvp.practice.settings;

import com.google.inject.Inject;
import country.pvp.practice.message.Messager;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Permission;
import me.vaperion.blade.command.annotation.Sender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PracticeSettingsCommand {

    private final @NotNull PracticeSettings practiceSettings;
    private final @NotNull PracticeSettingsService practiceSettingsService;

    @Command(value = "setspawn", description = "Sets the spawn location")
    @Permission("practice.admin")
    public void setSpawn(@Sender @NotNull Player sender) {
        practiceSettings.setSpawnLocation(sender.getLocation());
        practiceSettingsService.saveAsync(practiceSettings);
        Messager.messageSuccess(sender, "Successfully set the spawn location.");
    }

    @Command(value = "seteditor", description = "Sets the editor location")
    @Permission("practice.admin")
    public void setEditor(@Sender @NotNull Player sender) {
        practiceSettings.setEditorLocation(sender.getLocation());
        practiceSettingsService.saveAsync(practiceSettings);
        Messager.messageSuccess(sender, "Successfully set the editor location.");
    }
}
