package io.github.seggan.sfcalc;

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

public class Calculator {

    private final CalculatingAddon plugin;

    public Calculator(CalculatingAddon plugin) {
        this.plugin = plugin;
    }

    /**
     * Calculates the resourrces for the item and prints the out to the specified {@link CommandSender}
     *
     * @param sender the sender to send the calculation to
     * @param needed whether it sould print out how many are needed. Requires {@code sender instanceof Player}
     * to be {@code true}
     * @param item the Slimefun item to calculate
     * @param amount the amount to calculate for
     */
    public void printResults(@Nonnull CommandSender sender, boolean needed, @Nonnull SlimefunItem item, long amount) {
        Map<ItemStack, Long> results = calculate(item);

        StringRegistry registry = plugin.getStringRegistry();

        String header;
        String name = ChatColor.stripColor(ItemUtils.getItemName(item.getItem()));
        if (amount == 1) {
            header = String.format(registry.getHeaderString(), name);
        } else {
            header = Util.format(registry.getHeaderAmountString(), amount, name);
        }

        sender.sendMessage(header);

        // This will put our entries in order from lowest to highest
        List<Map.Entry<ItemStack, Long>> entries = new ArrayList<>(results.entrySet());
        entries.sort(Comparator.comparingLong(Map.Entry::getValue));

        if (needed && sender instanceof Player) {
            Map<ItemStack, Long> inv = getInventoryAsItemList((Player) sender);

            for (Map.Entry<ItemStack, Long> entry : entries) {
                Long inInventory = inv.getOrDefault(entry.getKey(), 0L);
                long originalValues = entry.getValue() * amount - inInventory;
                String parsedAmount;
                int maxStackSize = entry.getKey().getMaxStackSize();
                if (originalValues <= maxStackSize) {
                    parsedAmount = Long.toString(originalValues);
                } else {
                    parsedAmount = Util.format(registry.getStackString(), originalValues, (long) Math.floor((float) originalValues / maxStackSize), maxStackSize, originalValues % maxStackSize);
                }
                sender.sendMessage(Util.format(
                    registry.getNeededString(), parsedAmount, ChatColor.stripColor(ItemUtils.getItemName(entry.getKey()))));
            }
        } else {
            for (Map.Entry<ItemStack, Long> entry : entries) {
                long originalValues = entry.getValue() * amount;
                String parsedAmount;
                int maxStackSize = entry.getKey().getMaxStackSize();
                if (originalValues <= maxStackSize) {
                    parsedAmount = Long.toString(originalValues);
                } else {
                    parsedAmount = Util.format(registry.getStackString(), originalValues, (long) Math.floor(originalValues / (float) maxStackSize), maxStackSize, originalValues % maxStackSize);
                }
                sender.sendMessage(Util.format(
                    registry.getAmountString(), parsedAmount, ChatColor.stripColor(ItemUtils.getItemName(entry.getKey()))));
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
                // ingredient is null; it's a vanilla item
                add(recipe, i, 1);

            } else {

                if (ingredient.getRecipeType().getKey().getKey().equals("metal_forge")) {
                    add(recipe, new ItemStack(Material.DIAMOND), 9);
                }

                if (plugin.getBlacklistedIds().contains(ingredient.getId().toLowerCase(Locale.ROOT))) {
                    // it's a blacklisted item
                    add(recipe, i, 1);
                } else if (plugin.getBlacklistedRecipes().contains(ingredient.getRecipeType())) {
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
}
