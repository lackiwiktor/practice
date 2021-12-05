package country.pvp.practice;

import me.vaperion.blade.command.annotation.Flag;
import me.vaperion.blade.command.container.BladeCommand;
import me.vaperion.blade.command.container.BladeParameter;
import me.vaperion.blade.command.context.BladeContext;
import me.vaperion.blade.utils.MessageBuilder;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class HelpGenerator implements me.vaperion.blade.command.help.HelpGenerator {

    @Override
    public List<String> generate(
            BladeContext bladeContext, List<BladeCommand> list) {
        if (list.isEmpty()) return Collections.singletonList(ChatColor.RED + "Command not found.");

        List<String> help = new ArrayList<>(Collections.singletonList(ChatColor.YELLOW + "   Help:"));
        for (BladeCommand command : list) {
            help.add(getUsage(command, command.getAliases()[0]) + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + command.getDescription());
        }

        return help;
    }

    private String getUsage(BladeCommand command, String alias) {
        boolean hasDesc =
                command.getDescription() != null && !command.getDescription().trim().isEmpty();

        MessageBuilder builder =
                new MessageBuilder(ChatColor.GRAY + "/").append(ChatColor.GRAY + alias);

        Optional.of(command.getFlagParameters())
                .ifPresent(
                        flagParameters -> {
                            if (!flagParameters.isEmpty()) {
                                builder.append(" ").append(ChatColor.GRAY + "(");
                                if (hasDesc)
                                    builder.hover(
                                            Collections.singletonList(ChatColor.GRAY + command.getDescription().trim()));

                                int i = 0;
                                for (BladeParameter.FlagParameter flagParameter : flagParameters) {
                                    builder.append(i++ == 0 ? "" : (ChatColor.GRAY + " | ")).reset();
                                    if (hasDesc)
                                        builder.hover(
                                                Collections.singletonList(
                                                        ChatColor.GRAY + command.getDescription().trim()));

                                    Flag flag = flagParameter.getFlag();

                                    builder.append(ChatColor.AQUA + "-" + flag.value());
                                    if (!flagParameter.isBooleanFlag())
                                        builder.append(ChatColor.AQUA + " <" + flagParameter.getName() + ">");
                                    if (!flag.description().trim().isEmpty())
                                        builder.hover(
                                                Collections.singletonList(ChatColor.GRAY + flag.description().trim()));
                                }

                                builder.append(ChatColor.GRAY + ")");
                                if (hasDesc)
                                    builder.hover(
                                            Collections.singletonList(ChatColor.GRAY + command.getDescription().trim()));
                            }
                        });

        Optional.of(command.getCommandParameters())
                .ifPresent(
                        commandParameters -> {
                            if (!commandParameters.isEmpty()) {
                                builder.append(" ");
                                if (hasDesc)
                                    builder.hover(
                                            Collections.singletonList(ChatColor.GRAY + command.getDescription().trim()));

                                int i = 0;
                                for (BladeParameter.CommandParameter commandParameter : commandParameters) {
                                    builder.append(i++ == 0 ? "" : " ");

                                    builder.append(ChatColor.GRAY + (commandParameter.isOptional() ? "(" : "<"));
                                    builder.append(ChatColor.WHITE + commandParameter.getName());
                                    builder.append(ChatColor.GRAY + (commandParameter.isOptional() ? ")" : ">"));
                                }
                            }
                        });

        if (command.getExtraUsageData() != null && !command.getExtraUsageData().trim().isEmpty()) {
            builder.append(" ");
            builder.append(ChatColor.GRAY + command.getExtraUsageData());
            if (hasDesc)
                builder.hover(Collections.singletonList(ChatColor.GRAY + command.getDescription().trim()));
        }

        return builder.toStringFormat();
    }
}
