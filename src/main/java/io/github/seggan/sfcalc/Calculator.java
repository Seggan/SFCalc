package io.github.seggan.sfcalc;

import lombok.AllArgsConstructor;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static io.github.seggan.sfcalc.StringRegistry.format;

/**
 * The main class for the calculator
 *
 * @author Seggan
 * @author TheBusyBiscuit
 */
@AllArgsConstructor
public class Calculator {

    private final Set<RecipeType> blacklistedRecipes;
    private final Set<String> blacklistedIds;

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
        Map<ItemStack, Long> results = calculate(item);

        StringRegistry registry = SFCalc.inst().getStringRegistry();

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
                Long inInventory = inv.getOrDefault(entry.getKey(), 0L);
                long a = entry.getValue() * amount - inInventory;
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
                long originalValues = entry.getValue() * amount;
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
    }

    @Nonnull
    private Map<ItemStack, Long> getInventoryAsItemList(@Nonnull Player player) {
        Map<ItemStack, Long> inv = new HashMap<>();

        for (ItemStack item : player.getInventory().getContents()) {
            // if the Item is null or air, it will return null too
            if (item.getType().isAir()) {
                continue;
            }

            add(inv, item, item.getAmount());
        }

        return inv;
    }

    @Nonnull
    public Map<ItemStack, Long> calculate(@Nonnull SlimefunItem item) {
        Map<ItemStack, Long> result = new HashMap<>();
        Map<ItemStack, Map<ItemStack, Long>> calculated = new HashMap<>(); // stores names that are already calculated for reference

        for (ItemStack i : item.getRecipe()) {
            if (i == null) {
                // empty slot
                continue;
            }

            int amount = i.getAmount();

            if (calculated.containsKey(i)) { // check already calculated items
                addAll(result, calculated.get(i), amount);
                continue;
            }

            Map<ItemStack, Long> recipe = new HashMap<>();

            SlimefunItem ingredient = SlimefunItem.getByItem(i);

            if (ingredient == null) {
                // ingredient is null; it's a vanilla item
                add(recipe, i, 1);

            } else {
                if (ingredient.getRecipeType().getKey().getKey().toLowerCase(Locale.ROOT).equals("metal_forge")) {
                    add(recipe, new ItemStack(Material.DIAMOND), 9);
                    continue;
                }

                if (blacklistedIds.contains(ingredient.getId())) {
                    // it's a blacklisted item
                    add(recipe, i, 1);
                } else if (blacklistedRecipes.contains(ingredient.getRecipeType())) {
                    // item is a dust or a geo miner resource; just add it
                    add(recipe, i, 1);
                } else {
                    // item is a crafted Slimefun item; get its ingredients
                    addAll(recipe, calculate(ingredient), 1);
                }
            }

            calculated.put(i, recipe);
            addAll(result, recipe, amount);
        }

        return result;
    }

    private void add(@Nonnull Map<ItemStack, Long> map, @Nonnull ItemStack key, long amount) {
        map.merge(key, amount, Long::sum);
    }

    private void addAll(@Nonnull Map<ItemStack, Long> map, @Nonnull Map<ItemStack, Long> otherMap, long multiplier) {
        for (Map.Entry<ItemStack, Long> entry : otherMap.entrySet()) {
            add(map, entry.getKey(), entry.getValue() * multiplier);
        }
    }

    @Nonnull
    private static String getBasicName(ItemStack stack) {
        return ChatColor.stripColor(ItemUtils.getItemName(stack));
    }
}
