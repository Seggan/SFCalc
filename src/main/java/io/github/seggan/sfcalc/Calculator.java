package io.github.seggan.sfcalc;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static io.github.seggan.sfcalc.StringRegistry.format;

/**
 * The main class for the calculator
 *
 * @author Seggan
 * @author TheBusyBiscuit
 */
public class Calculator {
    private final SFCalc plugin;

    private final ThreadLocal<SlimefunItem> top = new ThreadLocal<>();

    public Calculator(SFCalc pl) {
        this.plugin = pl;
    }

    /**
     * Calculates the resources for the item and prints the out to the specified {@link CommandSender}
     *
     * @param sender the sender to send the calculation to
     * @param item   the Slimefun item to calculate
     * @param amount the amount to calculate for
     * @param needed whether it should print out how many are needed. Requires {@code sender instanceof Player}
     *               to be {@code true}
     */
    public void printResults(@Nonnull CommandSender sender, @Nonnull SlimefunItem item, long amount, boolean needed) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Map<ItemStack, Long> results = calculate(item, amount);

            StringRegistry registry = plugin.getStringRegistry();

            String header;
            String name = getBasicName(item.getItem());
            if (amount == 1) {
                header = format(registry.getHeaderString(), name);
            } else {
                header = format(registry.getHeaderAmountString(), name, amount);
            }

            sender.sendMessage(header);

            // This will put our entries in order from lowest to highest
            List<Map.Entry<ItemStack, Long>> entries = new ArrayList<>(results.entrySet());
            entries.sort(Comparator.comparingLong(Map.Entry::getValue));

            if (needed && sender instanceof Player) {
                Map<ItemStack, Long> inv = getInventoryAsItemList((Player) sender);

                for (Map.Entry<ItemStack, Long> entry : entries) {
                    long inInventory = inv.getOrDefault(entry.getKey(), 0L);
                    if (entry.getValue() <= 0) continue; //intermediate product/byproduct
                    long a = entry.getValue() - inInventory;
                    if (a < 0) a = 0;
                    String parsedAmount;
                    int maxStackSize = entry.getKey().getMaxStackSize();
                    if (a <= maxStackSize) {
                        parsedAmount = Long.toString(a);
                    } else {
                        parsedAmount = format(registry.getStackString(), a, (long) Math.floor((double) a / maxStackSize), maxStackSize, a % maxStackSize);
                    }
                    sender.sendMessage(format(registry.getNeededString(), getBasicName(entry.getKey()), parsedAmount));
                }
            } else {
                for (Map.Entry<ItemStack, Long> entry : entries) {
                    long originalValues = entry.getValue();
                    if (originalValues <= 0) continue;
                    String parsedAmount;
                    int maxStackSize = entry.getKey().getMaxStackSize();
                    if (originalValues <= maxStackSize) {
                        parsedAmount = Long.toString(originalValues);
                    } else {
                        parsedAmount = format(registry.getStackString(), originalValues, (long) Math.floor(originalValues / (float) maxStackSize), maxStackSize, originalValues % maxStackSize);
                    }
                    sender.sendMessage(format(registry.getAmountString(), getBasicName(entry.getKey()), parsedAmount));
                }
            }
        });
    }

    @Nonnull
    private Map<ItemStack, Long> getInventoryAsItemList(@Nonnull Player player) {
        Map<ItemStack, Long> inv = new HashMap<>();

        for (ItemStack item : player.getInventory().getContents()) {
            // if the Item is null or air, it will return null too
            if (item == null || item.getType().isAir()) {
                continue;
            }
            add(inv, item, item.getAmount());
        }

        return inv;
    }

    @Nonnull
    public Map<ItemStack, Long> calculate(@Nonnull SlimefunItem parent, long amount) {
        top.set(parent);
        Map<ItemStack, Long> result = new HashMap<>();

        // uncraft the material first to bypass the blacklist
        int multiplier = parent.getRecipeOutput().getAmount();
        long operations = (amount + multiplier - 1) / multiplier; // ceiling(needed/multiplier) but abusing fast ints
        for (ItemStack item : parent.getRecipe()) {
            if (item == null) continue;
            add(result, item, item.getAmount() * operations);
        }

        // uncraft submaterials
        SlimefunItem next = getNextItem(result);
        while (next != null) {
            multiplier = next.getRecipeOutput().getAmount();
            operations = (result.get(next.getItem()) + multiplier - 1) / multiplier; // ceiling(needed/multiplier) but abusing fast ints
            add(result, next.getItem(), -(multiplier * operations));
            for (ItemStack item : next.getRecipe()) {
                if (item == null) continue;
                add(result, item, item.getAmount() * operations);
            }
            next = getNextItem(result);
        }
        top.remove();

        return result;
    }

    /**
     * Gets the next item of a map that needs to be uncrafted. Returns null if no items are found.
     * An item needs to be uncrafted if
     * - it is a slimefun item
     * - there is a positive amount in the map(still requires crafting), and
     * - it is not blacklisted.
     */
    @Nullable
    private SlimefunItem getNextItem(Map<ItemStack, Long> map) {
        for (Map.Entry<ItemStack, Long> entry : map.entrySet()) {
            SlimefunItem item = SlimefunItem.getByItem(entry.getKey());
            if (item != null) {
                if (!plugin.getBlacklistedRecipes().contains(item.getRecipeType())
                        &&
                        !plugin.getBlacklistedIds().contains(item.getId())
                        &&
                        top.get() != item
                ) {
                    if (entry.getValue() > 0) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    private void add(@Nonnull Map<ItemStack, Long> map, @Nonnull ItemStack key, long amount) {
        ItemStack clone = key.clone();
        clone.setAmount(1);
        map.merge(clone, amount, Long::sum);
    }

    @Nonnull
    private String getBasicName(ItemStack stack) {
        return ChatColor.stripColor(ItemUtils.getItemName(stack));
    }
}
