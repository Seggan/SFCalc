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
                    int slots = SFCalc.getSlots(category.getItems().size());
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
            e.setCancelled(true);
            e.getWhoClicked().closeInventory();
            ItemStack clicked = e.getCurrentItem();
            SlimefunItem item = SlimefunItem.getByItem(clicked);
            if (item == null) {
                return;
            }

            CalcExecutor executor = new CalcExecutor(SFCalc.getInstance());
            executor.printResults(executor.calculate(item), e.getWhoClicked(), "sfcalc", item, 1);
        }
    }
}
