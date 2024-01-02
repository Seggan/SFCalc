package io.github.seggan.sfcalc;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.CommonPatterns;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import javax.annotation.Nonnull;
import java.util.*;

import static io.github.seggan.sfcalc.StringRegistry.format;

public class CalcCommand implements TabExecutor {

    private static final Set<String> ids = new HashSet<>();
    private final SFCalc plugin;

    public CalcCommand(SFCalc pl) {
        this.plugin = pl;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
        SFCalc.REPORTER.executeOrElseReport(() -> {
            long amount;
            String reqItem;
            SlimefunItem item;

            StringRegistry registry = plugin.getStringRegistry();

            if (args.length > 2 || args.length == 0) {
                return;
            }

            reqItem = args[0];

            if (args.length == 1) {
                amount = 1;
            } else if (!CommonPatterns.NUMERIC.matcher(args[1]).matches()) {
                sender.sendMessage(format(registry.getNotANumberString()));
                return;
            } else {
                try {
                    amount = Long.parseLong(args[1]);
                    if (amount == 0 || amount > Integer.MAX_VALUE) {
                        sender.sendMessage(format(registry.getInvalidNumberString()));
                        return;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(format(registry.getInvalidNumberString()));
                    return;
                }
            }

            item = SlimefunItem.getById(reqItem.toUpperCase(Locale.ROOT));

            if (item == null) {
                sender.sendMessage(format(registry.getNoItemString()));
                return;
            }

            SFCalcMetrics.addItemSearched(item.getItemName());

            plugin.getCalc().printResults(sender, item, amount, false);
        });
        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String alias, @Nonnull String[] args) {
        List<String> tabs = new ArrayList<>();
        SFCalc.REPORTER.executeOrElseReport(() -> {
            if (ids.isEmpty()) {
                for (SlimefunItem item : Slimefun.getRegistry().getEnabledSlimefunItems()) {
                    ids.add(item.getId().toLowerCase(Locale.ROOT));
                }
            }

            if (args.length == 1) {
                for (String id : ids) {
                    if (id.contains(args[0].toLowerCase(Locale.ROOT))) {
                        tabs.add(id);
                    }
                }
            }
        });
        return tabs;
    }
}
