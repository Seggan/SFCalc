package io.github.seggan.sfcalc;

import io.github.mooy1.infinitylib.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

import static io.github.seggan.sfcalc.StringRegistry.*;

public class CalcCommand extends SubCommand {

    public static final Pattern NUMBER = Pattern.compile("\\d+");

    private static final List<String> ids = new ArrayList<>();
    private final SFCalc plugin;

    public CalcCommand(SFCalc pl) {
        super("calc", "Calculates the resources needed for a given item", false);
        this.plugin = pl;
    }

    @Override
    public void execute(@Nonnull CommandSender sender, @Nonnull String[] args) {
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
        } else if (!NUMBER.matcher(args[1]).matches()) {
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
    }

    @Override
    public void complete(@Nonnull CommandSender sender, @Nonnull String[] args, @Nonnull List<String> tabs) {
        if (ids.isEmpty()) {
            for (SlimefunItem item : Slimefun.getRegistry().getEnabledSlimefunItems()) {
                ids.add(item.getId().toLowerCase(Locale.ROOT));
            }
        }

        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], ids, tabs);
        }
    }
}
