package io.github.seggan.sfcalc;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.PatternUtils;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class CalcExecutor implements CommandExecutor {
    private final SFCalc plugin;

    private final Map<String, SlimefunItem[]> exceptions = new HashMap<>();


    public CalcExecutor(SFCalc plugin) {
        this.plugin = plugin;

        SlimefunItem[] steelPlateRecipe = new SlimefunItem[8];
        SlimefunItem steel = SlimefunItem.getByID("STEEL_INGOT");
        for (int n = 0; n < 8; n++) {
            steelPlateRecipe[n] = steel;
        }
        exceptions.put("steel_plate", steelPlateRecipe);

        SlimefunItem[] reinforcedPlateRecipe = new SlimefunItem[8];
        SlimefunItem alloy = SlimefunItem.getByID("REINFORCED_ALLOY_INGOT");
        for (int n = 0; n < 8; n++) {
            steelPlateRecipe[n] = steel;
        }
        exceptions.put("steel_plate", steelPlateRecipe);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        int amount;
        String reqItem;
        SlimefunItem item;

        if (args.length > 2) {
            return false;
        }

        if (args.length == 0) {
            if (sender instanceof Player) {
                openGUI((Player) sender);
                return true;
            } else {
                return false;
            }
        }

        reqItem = args[0];

        if (args.length == 1) {
            amount = 1;
        } else {
            if (PatternUtils.NUMERIC.matcher(args[1]).matches()) {
                amount = Integer.parseInt(args[1]);
            } else {
                sender.sendMessage(ChatColor.RED + "That's not a number!");
                return true;
            }
        }


        reqItem = reqItem.toUpperCase();

        item = SlimefunItem.getByID(reqItem);

        if (item == null) {
            sender.sendMessage(plugin.noItemString);
            return true;
        }

        Calculator.printResults(Calculator.calculate(item, plugin), sender, s, item, amount, plugin);

        return true;
    }

    private void openGUI(Player player) {
        int size = Util.getSlots(SlimefunPlugin.getRegistry().getCategories().size());
        if (size > 54) {
            player.sendMessage(plugin.tooManyCategoriesString);
            return;
        }
        Inventory inv = Bukkit.createInventory(
                null,
                size,
                "Choose a Category"
        );
        for (Category category : SlimefunPlugin.getRegistry().getCategories()) {
            inv.addItem(category.getItem(player));
        }
        player.openInventory(inv);
    }


}
