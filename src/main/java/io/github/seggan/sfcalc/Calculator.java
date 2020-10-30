package io.github.seggan.sfcalc;

import me.mrCookieSlime.CSCoreLibPlugin.cscorelib2.inventory.ItemUtils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;
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


    static HashMap<String, Long> calculate(SlimefunItem item, SFCalc plugin) {
        HashMap<String, Long> result = new HashMap<>();

        for (ItemStack i : item.getRecipe()) {
            if (i == null) {
                // empty slot
                continue;
            }

            SlimefunItem ingredient = SlimefunItem.getByItem(i);

            if (ingredient == null) {
                // ingredient is null; it's a normal Minecraft item
                put(ItemUtils.getItemName(i), i.getAmount(), result);
                continue;
            }

            if (ingredient.getRecipeType().getKey().getKey().equals("metal_forge")) {
                put("diamond", 9, result);
            }

            if (plugin.blacklistedIds.contains(ingredient.getID().toLowerCase())) {
                // it's a blacklisted item
                put(ingredient.getItemName(), i.getAmount(), result);
                continue;
            }

            if (!plugin.blacklistedRecipes.contains(ingredient.getRecipeType())) {
                // item is a crafted Slimefun item; get its ingredients
                putAll(result, calculate(ingredient, plugin), i.getAmount());
            } else {
                // item is a dust or a geo miner resource; just add it
                put(ChatColor.stripColor(ingredient.getItemName()), i.getAmount(), result);
            }
        }

        return result;
    }

    static void printResults(HashMap<String, Long> results, CommandSender sender, String s, SlimefunItem item, Long amount, SFCalc plugin) {
        sender.sendMessage(String.format(
                plugin.headerString,
                amount + " " + Util.capitalize(ChatColor.stripColor(item.getItemName()))
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
            for (String name : results.keySet()) {
                sender.sendMessage(Util.format(
                        plugin.neededString,
                        results.get(name) * amount - Collections.frequency(sfInv, name),
                        Util.capitalize(name)
                ));
            }
        } else {
            for (String name : results.keySet()) {
                sender.sendMessage(Util.format(
                        plugin.amountString,
                        results.get(name) * amount,
                        Util.capitalize(name)
                ));
            }
        }
    }

    /**
     * This method will add the item to the hashmap
     *
     * @param id id of item
     */
    static void put(String id, int amount, HashMap<String, Long> result) {
        result.put(ChatColor.stripColor(id).toLowerCase(), result.getOrDefault(id, 0L) + amount);
    }

    /**
     * This method adds all items to the target from another hashmap
     *
     * @param target target hashmap
     * @param from other hashmap
     * @param amount amount of each item in map
     */
    static void putAll(HashMap<String, Long> target, HashMap<String, Long> from, int amount) {
        for (String key : from.keySet()) {
            target.put(key, (target.getOrDefault(key, 0L) + from.get(key)) * amount);
        }
    }
}
