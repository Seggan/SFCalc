package io.github.seggan.sfcalc;

import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static io.github.seggan.sfcalc.StringRegistry.format;

/**
 * The main class for the calculator
 *
 * @author Seggan
 * @author TheBusyBiscuit
 */
public class Calculator {
    private SFCalc plugin;

    public Calculator(SFCalc pl) {
        this.plugin = pl;
    }
    /**
     * Calculates the resources for the item and prints the out to the specified {@link CommandSender}
     *
     * @param sender the sender to send the calculation to
     * @param item the Slimefun item to calculate
     * @param amount the amount to calculate for
     * @param needed whether it should print out how many are needed. Requires {@code sender instanceof Player}
     * to be {@code true}
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
                    if(entry.getValue() <= 0) continue; //intermediate product/byproduct
                    long a = entry.getValue() - inInventory;
                    if(a < 0) a = 0;
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
                    if(originalValues <= 0) continue;
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

        Map<ItemStack, Long> result = new HashMap<>();
        add(result, parent.getItem(), amount);

        //uncraft the material
        add(result, parent.getItem(), -parent.getRecipeOutput().getAmount());
        for(ItemStack item : parent.getRecipe()) {
            if(item == null) continue;
            add(result, item, item.getAmount());
        }

        //uncraft submaterials
        SlimefunItemStack next = getNextItem(result);
        while(next != null) {
            int multiplier = next.getItem().getRecipeOutput().getAmount();
            long operations = (result.get(next)+multiplier-1)/multiplier; //ceiling(needed/multiplier) but abusing fast ints
            add(result, next, -(multiplier*operations));
            for(ItemStack item : next.getItem().getRecipe()) {
                if(item == null) continue;
                add(result, item, item.getAmount() * operations);
            }
            next = getNextItem(result);
        }

        return result;
    }

    /**
     * Gets the next item of a map that needs to be uncrafted. Returns null if no items are found.
     * An item needs to be uncrafted if
     * - it is a slimefun item
     * - there is a positive amount in the map(still requires crafting), and
     * - it is not blacklisted.
     *
     * @param map
     * @return
     */
    @Nullable
    private SlimefunItemStack getNextItem(Map<ItemStack, Long> map) {
        for(Map.Entry<ItemStack, Long> entry : map.entrySet()) {
            if(entry.getKey() instanceof SlimefunItemStack) {
                SlimefunItemStack ingredient = (SlimefunItemStack)entry.getKey();
                if (!plugin.getBlacklistedIds().contains(ingredient.getItemId()) &&
                        !plugin.getBlacklistedRecipes().contains(ingredient.getItem().getRecipeType())) {
                    if(entry.getValue() > 0) {
                        return ingredient;
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

    private void addAll(@Nonnull Map<ItemStack, Long> map, @Nonnull Map<ItemStack, Long> otherMap, long multiplier) {
        for (Map.Entry<ItemStack, Long> entry : otherMap.entrySet()) {
            add(map, entry.getKey(), entry.getValue() * multiplier);
        }
    }

    @Nonnull
    private String getBasicName(ItemStack stack) {
        return ChatColor.stripColor(ItemUtils.getItemName(stack));
    }
}
