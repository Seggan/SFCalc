package io.github.seggan.sfcalc;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class CalcHandler implements Listener {
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("Choose a Category")) {
            e.setCancelled(true);
            e.getWhoClicked().closeInventory();
            ItemStack clicked = e.getCurrentItem();

            if (clicked == null) {
                return;
            }
            for (Category category : SlimefunPlugin.getRegistry().getCategories()) {
                if (ChatColor.stripColor(category.getUnlocalizedName()).equals(
                        ChatColor.stripColor(Objects.requireNonNull(clicked.getItemMeta()).getDisplayName()))) {
                    int slots = Util.getSlots(category.getItems().size());
                    if (slots > 54) {
                        e.getWhoClicked().sendMessage(SFCalc.getInstance().tooManyItemsString);
                    }
                    Inventory newInv = Bukkit.createInventory(
                            null,
                            slots,
                            "Choose an Item"
                    );
                    for (SlimefunItem item : category.getItems()) {
                        newInv.addItem(item.getItem());
                    }
                    e.getWhoClicked().openInventory(newInv);
                }
            }
        } else if (e.getView().getTitle().equals("Choose an Item")) {
            SFCalc plugin = SFCalc.getInstance();
            e.setCancelled(true);
            e.getWhoClicked().closeInventory();
            ItemStack clicked = e.getCurrentItem();
            SlimefunItem item = SlimefunItem.getByItem(clicked);
            if (item == null) {
                return;
            }

            Calculator.printResults(Calculator.calculate(item, plugin), e.getWhoClicked(), "sfcalc", item, 1, plugin);
        }
    }
}
