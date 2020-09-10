package io.github.seggan.sfcalc;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.utils.PatternUtils;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class CalcExecutor implements CommandExecutor {
    private final SFCalc plugin;

    private final Map<String, SlimefunItem[]> exceptions = new HashMap<>();


    public CalcExecutor(SFCalc plugin) {
        this.plugin = plugin;

        SlimefunItem[] steelPlateRecipe = new SlimefunItem[8];
        SlimefunItem steel = SlimefunItem.getByID("STEEL_INGOT");
        for (int n = 0; n < 8; n++) {
            steelPlateRecipe[n] = steel;
        }
        exceptions.put("steel_plate", steelPlateRecipe);

        SlimefunItem[] reinforcedPlateRecipe = new SlimefunItem[8];
        SlimefunItem alloy = SlimefunItem.getByID("REINFORCED_ALLOY_INGOT");
        for (int n = 0; n < 8; n++) {
            steelPlateRecipe[n] = steel;
        }
        exceptions.put("steel_plate", steelPlateRecipe);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        int amount;
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
        } else {
            if (PatternUtils.NUMERIC.matcher(args[1]).matches()) {
                amount = Integer.parseInt(args[1]);
            } else {
                sender.sendMessage(ChatColor.RED + "That's not a number!");
                return true;
            }
        }


        reqItem = reqItem.toUpperCase();

        item = SlimefunItem.getByID(reqItem);

        if (item == null) {
            sender.sendMessage(ChatColor.RED + "That item was not found.");
            return true;
        }

        printResults(calculate(item), sender, s, item, amount);

        return true;
    }

    void printResults(List<String> results, CommandSender sender, String s, SlimefunItem item, int amount) {
        Set<String> resultSet = new HashSet<>(results);

        sender.sendMessage(String.format(
                plugin.headerString,
                capitalize(ChatColor.stripColor(item.getItemName()))
        ));
        if (s.equalsIgnoreCase("sfcalc")) {
            for (String name : resultSet) {
                sender.sendMessage(format(
                        plugin.amountString,
                        Collections.frequency(results, name) * amount,
                        capitalize(name.replace("_", " ").toLowerCase())
                ));
            }
        } else {
            if (sender instanceof Player) {
                PlayerInventory inv = ((Player) sender).getInventory();
                List<String> sfInv = new ArrayList<>();
                for (ItemStack i : inv.getContents()) {
                    if (i == null) {
                        continue;
                    }

                    SlimefunItem sfItem = SlimefunItem.getByItem(i);

                    if (sfItem == null) {
                        continue;
                    }

                    sfInv.add(sfItem.getItemName());
                }
                for (String name : resultSet) {
                    sender.sendMessage(format(
                            plugin.amountString,
                            Collections.frequency(results, name) * amount - Collections.frequency(sfInv, name),
                            capitalize(name.replace("_", " "))
                    ));
                }
            } else {
                sender.sendMessage("You have to be a player to send this message!");
            }
        }
    }

    List<String> calculate(SlimefunItem item) {
        List<String> result = new ArrayList<>();

        switch (item.getID().toLowerCase()) {
            case "carbon":
                for (int n = 0; n < 8; n++) {
                    result.add("coal");
                }
                break;
            case "compressed_carbon":
                for (int n = 0; n < 4; n++) {
                    result.addAll(calculate(SlimefunItem.getByID("CARBON")));
                }
                break;
            case "reinforced_plate":
                for (int n = 0; n < 8; n++) {
                    result.addAll(calculate(SlimefunItem.getByID("REINFORCED_ALLOY_INGOT")));
                }
                break;
            case "steel_plate":
                for (int n = 0; n < 8; n++) {
                    result.addAll(calculate(SlimefunItem.getByID("STEEL_INGOT")));
                }
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
                        result.add(i.getType().toString());
                        continue;
                    }

                    if (plugin.blacklistedIds.contains(ingredient.getID().toLowerCase())) {
                        // it's a blacklisted item
                        result.add(ChatColor.stripColor(ingredient.getItemName()));
                        continue;
                    }

                    if (!plugin.blacklistedRecipes.contains(ingredient.getRecipeType())) {
                        // item is a crafted Slimefun item; get its ingredients
                        result.addAll(calculate(ingredient));
                    } else {
                        // item is a dust or a geo miner resource; just add it
                        result.add(ChatColor.stripColor(ingredient.getItemName()));
                    }
                }
        }

        return result;
    }

    private static String capitalize(String s) {
        StringBuilder capped = new StringBuilder();
        String string = s.trim();

        for (int i = 0; i < string.length(); i++){
            char c = string.charAt(i);

            if (i == 0) {
                capped.append(Character.toUpperCase(c));
                continue;
            }

            c = Character.toLowerCase(c);

            if (string.charAt(i - 1) == ' ') {
                c = Character.toUpperCase(c);
            }

            capped.append(c);
        }

        return capped.toString();
    }

    private void openGUI(Player player) {
        int size = SFCalc.getSlots(SlimefunPlugin.getRegistry().getCategories().size());
        if (size > 54) {
            player.sendMessage(ChatColor.RED + "That many categories is not supported yet. Please use the command form of the calculator.");
            return;
        }
        Inventory inv = Bukkit.createInventory(
                null,
                size,
                "Choose a Category"
        );
        for (Category category : SlimefunPlugin.getRegistry().getCategories()) {
            inv.addItem(category.getItem(player));
        }
        player.openInventory(inv);
    }

    private String format(String s, int a, String i) {
        return s.replace("%d", Integer.toString(a)).replace("%s", i);
    }

}
