package io.github.seggan.sfcalc;

import io.github.mooy1.infinitylib.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.CommonPatterns;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nonnull;

import static io.github.seggan.sfcalc.StringRegistry.*;

public class NeededCommand extends SubCommand {

    private static final Set<String> ids = new HashSet<>();
    private final SFCalc plugin;

    public NeededCommand(SFCalc pl) {
        super("needed", "Tells you how much more resources are needed", false);
        this.plugin = pl;
    }

    @Override
    public void execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        SFCalc.REPORTER.executeOrElseReport(() -> {
            long amount;
            String reqItem;
            SlimefunItem item;

            StringRegistry registry = plugin.getStringRegistry();

            if (!(sender instanceof Player)) {
                sender.sendMessage(format(registry.getNotAPlayerString()));
                return;
            }

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

            item = SlimefunItem.getById(reqItem.toUpperCase());

            if (item == null) {
                sender.sendMessage(format(registry.getNoItemString()));
                return;
            }

            SFCalcMetrics.addItemSearched(item.getItemName());

            plugin.getCalc().printResults(sender, item, amount, true);
        });
    }

    @Override
    public void complete(@Nonnull CommandSender sender, @Nonnull String[] args, @Nonnull List<String> tabs) {
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
    }
}
