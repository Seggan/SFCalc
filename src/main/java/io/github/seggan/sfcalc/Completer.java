package io.github.seggan.sfcalc;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Completer implements TabCompleter {
    List<String> commands = new ArrayList<>();

    public Completer() {
        Field[] fields = SlimefunItems.class.getFields();
        for (Field f : fields) {
            if (Modifier.isStatic(f.getModifiers())) {
                commands.add(f.getName().toLowerCase());
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], commands, completions);
        }

        Collections.sort(completions);

        return completions;
    }
}
