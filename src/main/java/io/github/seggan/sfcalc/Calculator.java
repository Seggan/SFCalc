package io.github.seggan.sfcalc;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public final class Calculator {

    static List<String> calculate(SlimefunItem item, SFCalc plugin) {
        List<String> result = new ArrayList<>();

        switch (item.getID().toLowerCase()) {
            case "carbon":
                for (int n = 0; n < 8; n++) {
                    result.add("coal");
                }
                break;
            case "compressed_carbon":
                for (int n = 0; n < 4; n++) {
                    result.addAll(calculate(SlimefunItem.getByID("CARBON"), plugin));
                }
                break;
            case "reinforced_plate":
                for (int n = 0; n < 8; n++) {
                    result.addAll(calculate(SlimefunItem.getByID("REINFORCED_ALLOY_INGOT"), plugin));
                }
                break;
            case "steel_plate":
                for (int n = 0; n < 8; n++) {
                    result.addAll(calculate(SlimefunItem.getByID("STEEL_INGOT"), plugin));
                }
                break;
            default:
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

                    if (plugin.blacklistedIds.contains(ingredient.getID().toLowerCase())) {
                        // it's a blacklisted item
                        result.add(ChatColor.stripColor(ingredient.getItemName()));
                        continue;
                    }

                    if (!plugin.blacklistedRecipes.contains(ingredient.getRecipeType())) {
                        // item is a crafted Slimefun item; get its ingredients
                        result.addAll(calculate(ingredient, plugin));
                    } else {
                        // item is a dust or a geo miner resource; just add it
                        result.add(ChatColor.stripColor(ingredient.getItemName()));
                    }
                }
        }

        return result;
    }

    static void printResults(List<String> results, CommandSender sender, String s, SlimefunItem item, int amount, SFCalc plugin) {
        Set<String> resultSet = new HashSet<>(results);

        sender.sendMessage(String.format(
                plugin.headerString,
                Util.capitalize(ChatColor.stripColor(item.getItemName()))
        ));
        if (s.equalsIgnoreCase("sfcalc")) {
            for (String name : resultSet) {
                sender.sendMessage(Util.format(
                        plugin.amountString,
                        Collections.frequency(results, name) * amount,
                        Util.capitalize(name.replace("_", " ").toLowerCase())
                ));
            }
        } else {
            if (sender instanceof Player) {
                PlayerInventory inv = ((Player) sender).getInventory();
                List<String> sfInv = new ArrayList<>();
                for (ItemStack i : inv.getContents()) {
                    if (i == null) {
                        continue;
                    }

                    SlimefunItem sfItem = SlimefunItem.getByItem(i);

                    if (sfItem == null) {
                        continue;
                    }

                    sfInv.add(sfItem.getItemName());
                }
                for (String name : resultSet) {
                    sender.sendMessage(Util.format(
                            plugin.amountString,
                            Collections.frequency(results, name) * amount - Collections.frequency(sfInv, name),
                            Util.capitalize(name.replace("_", " "))
                    ));
                }
            } else {
                sender.sendMessage("You have to be a player to send this message!");
            }
        }
    }



}
