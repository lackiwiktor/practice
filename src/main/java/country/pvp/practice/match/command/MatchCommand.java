package country.pvp.practice.match.command;

import com.google.inject.Inject;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchMenuProvider;
import country.pvp.practice.message.Messager;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Name;
import me.vaperion.blade.command.annotation.Optional;
import me.vaperion.blade.command.annotation.Sender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class MatchCommand {

    private final MatchMenuProvider matchMenuProvider;

    @Command("match list")
    public void list(@Sender Player sender) {
        matchMenuProvider.provide().openMenu(sender);
    }

    @Command("match cancel")
    public void cancel(@Sender Player sender, @Name("match") Match match, @Optional("Cancelled by the staff member") @Name("reason") String reason) {
        match.cancel(reason);
        Messager.messageSuccess(sender, "Successfully cancelled this match.");
    }
}
