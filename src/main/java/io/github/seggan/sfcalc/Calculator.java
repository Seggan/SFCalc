package io.github.seggan.sfcalc;

import me.mrCookieSlime.CSCoreLibPlugin.cscorelib2.inventory.ItemUtils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Calculator {
//    Copyright (C) 2020 Seggan
//    Email: segganew@gmail.com
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//            (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.

    public static List<String> calculate(SlimefunItem item, SFCalc plugin) {
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
                        result.add(ItemUtils.getItemName(i));
                        continue;
                    }

                    if (ingredient.getRecipeType().getKey().getKey().equals("metal_forge")) {
                        for (int n = 0; n < 9; n++) {
                            result.add("diamond");
                        }
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

    static void printResults(List<String> results, CommandSender sender, String s, SlimefunItem item, Long amount, SFCalc plugin) {
        Set<String> resultSet = new HashSet<>(results);

        sender.sendMessage(String.format(
                plugin.headerString,
                Util.capitalize(ChatColor.stripColor(item.getItemName()))
        ));

        if (s.equals("sfneeded")) {
            List<String> sfInv = new ArrayList<>();
            for (ItemStack i : ((Player) sender).getInventory().getContents()) {
                if (i == null) {
                    continue;
                }

                SlimefunItem sfItem = SlimefunItem.getByItem(i);

                if (sfItem == null) {
                    continue;
                }

                for (int n = 0; n < i.getAmount(); n++) {
                    sfInv.add(ChatColor.stripColor(sfItem.getItemName()));
                }
            }
            for (String name : resultSet) {
                sender.sendMessage(Util.format(
                        plugin.neededString,
                        Collections.frequency(results, name) * amount - Collections.frequency(sfInv, name),
                        Util.capitalize(name)
                ));
            }
        } else {
            for (String name : resultSet) {
                sender.sendMessage(Util.format(
                        plugin.amountString,
                        Collections.frequency(results, name) * amount,
                        Util.capitalize(name)
                ));
            }
        }
    }



}
