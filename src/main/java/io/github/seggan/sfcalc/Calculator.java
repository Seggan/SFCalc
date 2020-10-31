package io.github.seggan.sfcalc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.mrCookieSlime.CSCoreLibPlugin.cscorelib2.inventory.ItemUtils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;

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
        Map<String, Long> results = calculate(item);

        sender.sendMessage(String.format(plugin.headerString, Util.capitalize(ChatColor.stripColor(item.getItemName()))));

        if (command.equals("sfneeded") && sender instanceof Player) {
            List<String> sfInv = getInventoryAsItemList((Player) sender);

            for (Map.Entry<String, Long> entry : results.entrySet()) {
                int inInventory = Collections.frequency(sfInv, entry.getKey());
                sender.sendMessage(Util.format(plugin.neededString, entry.getValue() * amount - inInventory, Util.capitalize(entry.getKey())));
            }
        } else {
            for (Map.Entry<String, Long> entry : results.entrySet()) {
                sender.sendMessage(Util.format(plugin.amountString, entry.getValue() * amount, Util.capitalize(entry.getKey())));
            }
        }
    }

    private List<String> getInventoryAsItemList(Player player) {
        List<String> list = new ArrayList<>();

        for (ItemStack item : player.getInventory().getContents()) {
            SlimefunItem sfItem = SlimefunItem.getByItem(item);

            // if the Item is null or air, it will return null too
            if (sfItem == null) {
                continue;
            }

            for (int n = 0; n < item.getAmount(); n++) {
                list.add(ChatColor.stripColor(sfItem.getItemName()));
            }
        }

        return list;
    }

    private Map<String, Long> calculate(SlimefunItem item) {
        Map<String, Long> result = new HashMap<>();

        switch (item.getId().toLowerCase(Locale.ROOT)) {
        case "carbon":
            add(result, "coal", 8);
            break;
        case "compressed_carbon":
            addAll(result, calculate(SlimefunItem.getByID("CARBON")), 4);
            break;
        case "reinforced_plate":
            addAll(result, calculate(SlimefunItem.getByID("REINFORCED_ALLOY_INGOT")), 8);
            break;
        case "steel_plate":
            addAll(result, calculate(SlimefunItem.getByID("STEEL_INGOT")), 8);
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
                    add(result, ItemUtils.getItemName(i));
                    continue;
                }

                if (ingredient.getRecipeType().getKey().getKey().equals("metal_forge")) {
                    add(result, "diamond", 9);
                }

                if (plugin.blacklistedIds.contains(ingredient.getId().toLowerCase(Locale.ROOT))) {
                    // it's a blacklisted item
                    add(result, ChatColor.stripColor(ingredient.getItemName()));
                } else if (!plugin.blacklistedRecipes.contains(ingredient.getRecipeType())) {
                    // item is a crafted Slimefun item; get its ingredients
                    addAll(result, calculate(ingredient));
                } else {
                    // item is a dust or a geo miner resource; just add it
                    add(result, ChatColor.stripColor(ingredient.getItemName()));
                }
            }
        }

        return result;
    }

    private void add(Map<String, Long> map, String key) {
        add(map, key, 1);
    }

    private void add(Map<String, Long> map, String key, long amount) {
        map.merge(key, amount, Long::sum);
    }

    private void addAll(Map<String, Long> map, Map<String, Long> otherMap) {
        for (Map.Entry<String, Long> entry : otherMap.entrySet()) {
            add(map, entry.getKey(), entry.getValue());
        }
    }

    private void addAll(Map<String, Long> map, Map<String, Long> otherMap, long multiplier) {
        for (Map.Entry<String, Long> entry : otherMap.entrySet()) {
            add(map, entry.getKey(), entry.getValue() * multiplier);
        }
    }

}
