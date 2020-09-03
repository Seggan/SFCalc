package io.github.seggan.sfcalc;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Completer implements TabCompleter {
    private final List<String> commands = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (commands.isEmpty()) {
            for (SlimefunItem item : SlimefunPlugin.getRegistry().getEnabledSlimefunItems()) {
                commands.add(item.getID());
            }
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }

        Collections.sort(completions);

        return completions;
    }
}
