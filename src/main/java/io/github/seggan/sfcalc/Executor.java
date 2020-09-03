package io.github.seggan.sfcalc;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Executor implements CommandExecutor {
    private final SFCalc plugin;


    public Executor(SFCalc plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        int amount;
        String reqItem;
        SlimefunItem item;

        if (args.length > 2 || args.length == 0) {
            return false;
        }

        reqItem = args[0];

        if (args.length == 1) {
            amount = 1;
        } else {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "That's not a number!");
                return true;
            }
        }

        reqItem = reqItem.toUpperCase();

        item = SlimefunItem.getByID(reqItem);

        if (item == null) {
            sender.sendMessage(ChatColor.RED + "That item was not found.");
            return true;
        }

        ArrayList<String> result = calculate(item);
        HashSet<String> resultSet = new HashSet<>(result);

        sender.sendMessage(ChatColor.YELLOW + "Recipe for " + WordUtils.capitalizeFully(
                reqItem.replaceAll("_", " ") + ":"));

        for (String name : resultSet) {
            sender.sendMessage(ChatColor.YELLOW + String.format(
                    "%d of %s",
                    Collections.frequency(result, name) * amount,
                    WordUtils.capitalizeFully(name.replaceAll("_", " ").toLowerCase())
            ));
        }

        return true;
    }

    public ArrayList<String> calculate(SlimefunItem item) {
        ArrayList<String> result = new ArrayList<>();
        for (ItemStack i : item.getRecipe()) {
            if (i == null) {
                // empty slot
                continue;
            }

            SlimefunItem ingredient = SlimefunItem.getByItem(i);

            if (ingredient == null) {
                // ingredient is null; it's a normal Minecraft item
                result.add(i.getType().toString());
                continue;
            }

            if (!SFCalc.blacklistedRecipes.contains(ingredient.getRecipeType())) {
                // item is a crafted Slimefun item; get its ingredients
                ArrayList<String> subitems = calculate(ingredient);
                result.addAll(subitems);
            } else {
                // item is a dust or a geo miner resource; just add it
                result.add(ChatColor.stripColor(ingredient.getItemName()));
            }
        }

        return result;
    }
}
