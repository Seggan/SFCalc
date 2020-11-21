package io.github.seggan.sfcalc;

import me.mrCookieSlime.CSCoreLibPlugin.cscorelib2.inventory.ItemUtils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright (C) 2020 Seggan
 * Email: segganew@gmail.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
public class Calculator {

    private final SFCalc plugin;

    public Calculator(SFCalc plugin) {
        this.plugin = plugin;
    }

    public void printResults(CommandSender sender, String command, SlimefunItem item, long amount) {
        Map<String, Long> results = startCalculation(item);

        String header;
        String name = ChatColor.stripColor(ItemUtils.getItemName(item.getItem()));
        if (amount == 1) {
            header = String.format(plugin.headerString, name);
        } else {
            header = Util.format(plugin.headerAmountString, amount, name);
        }

        sender.sendMessage(header);

        // This will put our entries in order from lowest to highest
        List<Map.Entry<String, Long>> entries = new ArrayList<>(results.entrySet());
        entries.sort(Comparator.comparingLong(Map.Entry::getValue));

        if (command.equals("sfneeded") && sender instanceof Player) {
            Map<String, Long> inv = getInventoryAsItemList((Player) sender);

            for (Map.Entry<String, Long> entry : entries) {
                Long inInventory = inv.getOrDefault(entry.getKey(), 0L);
                sender.sendMessage(Util.format(plugin.neededString, entry.getValue() * amount - inInventory, Util.capitalize(entry.getKey())));
            }
        } else {
            for (Map.Entry<String, Long> entry : entries) {
                sender.sendMessage(Util.format(plugin.amountString, entry.getValue() * amount, Util.capitalize(entry.getKey())));
            }
        }
    }

    private Map<String, Long> getInventoryAsItemList(Player player) {
        Map<String, Long> inv = new HashMap<>();

        for (ItemStack item : player.getInventory().getContents()) {
            // if the Item is null or air, it will return null too
            if (item == null) {
                continue;
            }

            add(inv, ChatColor.stripColor(ItemUtils.getItemName(item)), item.getAmount());
        }

        return inv;
    }
    
    private Map<String, Long> startCalculation(SlimefunItem item) {
        
        if (item.getId().equals("UU_MATTER")) {
            Map<String, Long> uuRecipe = new HashMap<>();
            add(uuRecipe, "scrap", 200);
            return uuRecipe;
        }
        
        return calculate(item.getItem(), new HashMap<>());
    }

    private Map<String, Long> calculate(ItemStack i, Map<String, Map<String, Long>> cache) {
        Map<String, Long> result = new HashMap<>();

        String name = ChatColor.stripColor(ItemUtils.getItemName(i).toLowerCase());

        int amount = i.getAmount();
        
        // check if already calculated
        if (cache.containsKey(name)) {
            addAll(result, cache.get(name), amount);
            return result;
        }

        Map<String, Long> recipe = new HashMap<>();

        SlimefunItem item = SlimefunItem.getByItem(i);

        if (item == null || plugin.blacklistedNames.contains(name)) {
            // slimefun item is null (its a vanilla item) or name is blacklisted 
            add(recipe, name);

        } else {

            if (item.getRecipeType().getKey().getKey().equals("metal_forge")) {
                add(recipe, "diamond", 9);
            }

            // always get recipe if this is the item they are calculating
             if (!plugin.blacklistedRecipes.contains(item.getRecipeType().getKey().getKey()) || cache.size() == 0) {
                // item is a crafted Slimefun item; get its ingredients
                 boolean hasRecipe = false;
                 for (ItemStack ingredient : item.getRecipe()) {
                     if (ingredient != null) {
                         addAll(recipe, calculate(ingredient, cache));
                         hasRecipe = true;
                     }
                 }
                
                // item had empty recipe, just add it
                 if (!hasRecipe) {
                     add(recipe, name);
                 }
                
            } else {
                // item is a dust or a geo miner resource; just add it
                add(recipe, name);
            }
        }

        cache.put(name, recipe);
        addAll(result, recipe, amount);
        
        return result;
    }

    private void add(Map<String, Long> map, String key) {
        add(map, key, 1);
    }

    private void add(Map<String, Long> map, String key, long amount) {
        map.merge(key, amount, Long::sum);
    }

    private void addAll(Map<String, Long> map, Map<String, Long> otherMap) {
        addAll(map, otherMap, 1);
    }

    private void addAll(Map<String, Long> map, Map<String, Long> otherMap, long multiplier) {
        for (Map.Entry<String, Long> entry : otherMap.entrySet()) {
            add(map, entry.getKey(), entry.getValue() * multiplier);
        }
    }
}
