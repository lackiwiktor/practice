package country.pvp.practice.match.command;

import com.google.inject.Inject;
import country.pvp.practice.match.Match;
import country.pvp.practice.message.Messager;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.command.annotation.*;
import org.bukkit.entity.Player;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class MatchCommand {

    @Command("match cancel")
    public void cancel(@Sender Player sender, @Name("match") Match<?> match, @Combined @Optional("Cancelled by the staff member") @Name("reason") String reason) {
        match.cancel(reason);
        Messager.messageSuccess(sender, "Successfully cancelled this match.");
    }
}
