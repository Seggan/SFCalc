package io.github.seggan.sfcalc;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
        Map<ItemStack, Long> results = calculate(item);

        String header;
        String name = ChatColor.stripColor(ItemUtils.getItemName(item.getItem()));
        if (amount == 1) {
            header = String.format(plugin.headerString, name);
        } else {
            header = Util.format(plugin.headerAmountString, amount, name);
        }

        sender.sendMessage(header);

        // This will put our entries in order from lowest to highest
        List<Map.Entry<ItemStack, Long>> entries = new ArrayList<>(results.entrySet());
        entries.sort(Comparator.comparingLong(Map.Entry::getValue));
        
        if (command.equals("sfneeded") && sender instanceof Player) {
            Map<ItemStack, Long> inv = getInventoryAsItemList((Player) sender);

            for (Map.Entry<ItemStack, Long> entry : entries) {
                Long inInventory = inv.getOrDefault(entry.getKey(), 0L);
                long originalValues = entry.getValue() * amount - inInventory;
                String parsedAmount;
                int maxStackSize = entry.getKey().getMaxStackSize();
                if (originalValues <= maxStackSize) {
                    parsedAmount = Long.toString(originalValues);
                } else {
                    parsedAmount = Util.format(plugin.stackString, originalValues, (long) Math.floor((float) originalValues / maxStackSize), maxStackSize, originalValues % maxStackSize);
                }
                sender.sendMessage(Util.format(
                        plugin.neededString, parsedAmount, ChatColor.stripColor(ItemUtils.getItemName(entry.getKey()))));
            }
        } else {
            for (Map.Entry<ItemStack, Long> entry : entries) {
                long originalValues = entry.getValue() * amount;
                String parsedAmount;
                int maxStackSize = entry.getKey().getMaxStackSize();
                if (originalValues <= maxStackSize) {
                    parsedAmount = Long.toString(originalValues);
                } else {
                    parsedAmount = Util.format(plugin.stackString, originalValues, (long) Math.floor(originalValues / (float) maxStackSize), maxStackSize, originalValues % maxStackSize);
                }
                sender.sendMessage(Util.format(
                        plugin.amountString, parsedAmount, ChatColor.stripColor(ItemUtils.getItemName(entry.getKey()))));
            }
        }
    }

    private Map<ItemStack, Long> getInventoryAsItemList(Player player) {
        Map<ItemStack, Long> inv = new HashMap<>();

        for (ItemStack item : player.getInventory().getContents()) {
            // if the Item is null or air, it will return null too
            if (item == null) {
                continue;
            }

            add(inv, item, item.getAmount());
        }

        return inv;
    }

    private Map<ItemStack, Long> calculate(SlimefunItem item) {
        Map<ItemStack, Long> result = new HashMap<>();
        Map<ItemStack, Map<ItemStack, Long>> calculated = new HashMap<>(); //stores names that are already calculated for reference

        for (ItemStack i : item.getRecipe()) {
            if (i == null) {
                // empty slot
                continue;
            }

            int amount = i.getAmount();

            if (calculated.containsKey(i)) { //check already calculated items
                addAll(result, calculated.get(i), amount);
                continue;
            }

            Map<ItemStack, Long> recipe = new HashMap<>();

            SlimefunItem ingredient = SlimefunItem.getByItem(i);

            if (ingredient == null) {
                // ingredient is null; it's a normal Minecraft item
                add(recipe, i, 1);

            } else {

                if (ingredient.getRecipeType().getKey().getKey().equals("metal_forge")) {
                    add(recipe, new ItemStack(Material.DIAMOND), 9);
                }

                if (plugin.blacklistedIds.contains(ingredient.getId().toLowerCase(Locale.ROOT))) {
                    // it's a blacklisted item
                    add(recipe, i, 1);
                } else if (!plugin.blacklistedRecipes.contains(ingredient.getRecipeType())) {
                    // item is a crafted Slimefun item; get its ingredients
                    addAll(recipe, calculate(ingredient), 1);
                } else {
                    // item is a dust or a geo miner resource; just add it
                    add(recipe, i, 1);
                }
            }

            calculated.put(i, recipe);
            addAll(result, recipe, amount);

        }

        return result;
    }

    private void add(Map<ItemStack, Long> map, ItemStack key, long amount) {
        map.merge(key, amount, Long::sum);
    }

    private void addAll(Map<ItemStack, Long> map, Map<ItemStack, Long> otherMap, long multiplier) {
        for (Map.Entry<ItemStack, Long> entry : otherMap.entrySet()) {
            add(map, entry.getKey(), entry.getValue() * multiplier);
        }
    }
}
