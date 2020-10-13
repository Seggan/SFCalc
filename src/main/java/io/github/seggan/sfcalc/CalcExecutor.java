package io.github.seggan.sfcalc;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import io.github.thebusybiscuit.slimefun4.core.categories.FlexCategory;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.PatternUtils;
import me.mrCookieSlime.Slimefun.Objects.Category;
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
public class CalcExecutor implements CommandExecutor {

    private final SFCalc plugin;
    private final Map<String, SlimefunItem[]> exceptions = new HashMap<>();

    public CalcExecutor(SFCalc plugin) {
        this.plugin = plugin;

        // SlimefunItem[] steelPlateRecipe = new SlimefunItem[8];
        // SlimefunItem steel = SlimefunItem.getByID("STEEL_INGOT");
        // for (int n = 0; n < 8; n++) {
        // steelPlateRecipe[n] = steel;
        // }
        // exceptions.put("steel_plate", steelPlateRecipe);
        //
        // SlimefunItem[] reinforcedPlateRecipe = new SlimefunItem[8];
        // SlimefunItem alloy = SlimefunItem.getByID("REINFORCED_ALLOY_INGOT");
        // for (int n = 0; n < 8; n++) {
        // steelPlateRecipe[n] = steel;
        // }
        // exceptions.put("steel_plate", steelPlateRecipe);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player) && s.equals("sfneeded")) {
            sender.sendMessage(plugin.notAPlayerString);
            return true;
        }

        long amount;
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
        } else if (PatternUtils.NUMERIC.matcher(args[1]).matches()) {
            amount = Long.parseLong(args[1]);
        } else {
            sender.sendMessage(plugin.noNumberString);
            return true;
        }

        reqItem = reqItem.toUpperCase();
        item = SlimefunItem.getByID(reqItem);

        if (item == null) {
            sender.sendMessage(plugin.noItemString);
            return true;
        }

        plugin.itemsSearched.add(Util.capitalize(ChatColor.stripColor(item.getItemName())));

        Calculator calculator = new Calculator(plugin);
        calculator.printResults(sender, s, item, amount);

        return true;
    }

    private void openGUI(Player player) {
        int size = Util.getSlots(SlimefunPlugin.getRegistry().getCategories().size());

        if (size > 54) {
            player.sendMessage(plugin.tooManyCategoriesString);
            return;
        }

        Inventory inv = Bukkit.createInventory(null, size, "Choose a Category");

        for (Category category : SlimefunPlugin.getRegistry().getCategories()) {
            if (category instanceof FlexCategory) {
                continue;
            }

            inv.addItem(category.getItem(player));
        }

        player.openInventory(inv);
    }

}
