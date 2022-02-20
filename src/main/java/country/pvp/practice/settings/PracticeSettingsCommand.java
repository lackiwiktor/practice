package country.pvp.practice.settings;

import com.google.inject.Inject;
import country.pvp.practice.util.message.Sender;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Permission;
import org.bukkit.entity.Player;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PracticeSettingsCommand {

    private final PracticeSettings practiceSettings;
    private final PracticeSettingsRepository practiceSettingsRepository;

    @Command(value = "setspawn", description = "Sets the spawn location")
    @Permission("practice.admin")
    public void setSpawn(@me.vaperion.blade.command.annotation.Sender Player sender) {
        practiceSettings.setSpawnLocation(sender.getLocation());
        practiceSettingsRepository.saveAsync(practiceSettings);
        Sender.messageSuccess(sender, "Successfully set the spawn location.");
    }

    @Command(value = "seteditor", description = "Sets the editor location")
    @Permission("practice.admin")
    public void setEditor(@me.vaperion.blade.command.annotation.Sender Player sender) {
        practiceSettings.setEditorLocation(sender.getLocation());
        practiceSettingsRepository.saveAsync(practiceSettings);
        Sender.messageSuccess(sender, "Successfully set the editor location.");
    }
}
