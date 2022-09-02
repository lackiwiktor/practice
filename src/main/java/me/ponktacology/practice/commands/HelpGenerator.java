package me.ponktacology.practice.commands;

import me.vaperion.blade.annotation.argument.Flag;
import me.vaperion.blade.bukkit.util.MessageBuilder;
import me.vaperion.blade.command.Command;
import me.vaperion.blade.command.Parameter;
import me.vaperion.blade.context.Context;
import org.bukkit.ChatColor;

import java.util.*;

public class HelpGenerator implements me.vaperion.blade.platform.HelpGenerator {

    @Override
    public List<String> generate(Context bladeContext, List<Command> list) {
        if (list.isEmpty()) return Collections.singletonList(ChatColor.RED + "Command not found.");

        List<String> help = new ArrayList<>(Collections.singletonList(ChatColor.YELLOW + "   Help:"));
        list.sort(Comparator.comparing(o -> o.getAliases()[0]));
        for (Command command : list) {
            help.add(getUsage(command, command.getAliases()[0]) + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + command.getDescription());
        }

        return help;
    }

    private String getUsage(Command command, String alias) {
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
                                for (Parameter.FlagParameter flagParameter : flagParameters) {
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
                                for (Parameter.CommandParameter commandParameter : commandParameters) {
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
