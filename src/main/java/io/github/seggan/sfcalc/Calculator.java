package io.github.seggan.sfcalc;

import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import lombok.AllArgsConstructor;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.collections.Pair;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import static io.github.seggan.sfcalc.StringRegistry.format;

/**
 * The main class for the calculator
 *
 * @author Seggan
 * @author TheBusyBiscuit
 */
public class Calculator {

    private final static Map<Pair<ItemStack, Long>, Map<ItemStack, Long>> calculated = new HashMap<>();

    /**
     * Calculates the resources for the item and prints the out to the specified {@link CommandSender}
     *
     * @param sender the sender to send the calculation to
     * @param item the Slimefun item to calculate
     * @param amount the amount to calculate for
     * @param needed whether it should print out how many are needed. Requires {@code sender instanceof Player}
     * to be {@code true}
     */
    public static void printResults(@Nonnull CommandSender sender, @Nonnull SlimefunItem item, long amount, boolean needed) {
        Map<ItemStack, Long> results = calculate(item, amount);

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
    }

    @Nonnull
    private static Map<ItemStack, Long> getInventoryAsItemList(@Nonnull Player player) {
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
    public static Map<ItemStack, Long> calculate(@Nonnull SlimefunItem parent, Long amount) {
//        SFCalc.inst().log("calcing " + amount + " " + parent.getId());
        //check cache
        if(calculated.containsKey(new Pair<>(parent.getItem(), amount))) {
            return calculated.get(new Pair<>(parent.getItem(), amount));
        }


        Map<ItemStack, Long> result = new HashMap<>();
        add(result, parent.getItem(), amount);

        //decompose the material
        add(result, parent.getItem(), -parent.getRecipeOutput().getAmount());
        for(ItemStack item : parent.getRecipe()) {
            if(item == null) continue;
            add(result, item, item.getAmount());
        }

        SlimefunItemStack next = getNextItem(result);
        //calculate submaterials
        while(next != null) {
            add(result, next, -1);
            Map<ItemStack, Long> craft = calculate(next.getItem(), 1L);
            addAll(result, craft, 1);
            next = getNextItem(result);
        }
        //store cache
        calculated.put(new Pair<>(parent.getItem(), amount), result);
        return result;
    }

    @Nullable
    private static SlimefunItemStack getNextItem(Map<ItemStack, Long> map) {
        for(Map.Entry<ItemStack, Long> entry : map.entrySet()) {
            if(entry.getKey() instanceof SlimefunItemStack) {
                SlimefunItemStack ingredient = (SlimefunItemStack)entry.getKey();
                if (!SFCalc.getBlacklistedIds().contains(ingredient.getItemId()) &&
                        !SFCalc.getBlacklistedRecipes().contains(ingredient.getItem().getRecipeType())) {
                    if(entry.getValue() > 0) {
                        return ingredient;
                    }
                }
            }
        }
        return null;
    }

    private static void add(@Nonnull Map<ItemStack, Long> map, @Nonnull ItemStack key, long amount) {
        map.merge(key, amount, Long::sum);
    }

    private static void addAll(@Nonnull Map<ItemStack, Long> map, @Nonnull Map<ItemStack, Long> otherMap, long multiplier) {
        for (Map.Entry<ItemStack, Long> entry : otherMap.entrySet()) {
            add(map, entry.getKey(), entry.getValue() * multiplier);
        }
    }

    @Nonnull
    private static String getBasicName(ItemStack stack) {
        return ChatColor.stripColor(ItemUtils.getItemName(stack));
    }
}
