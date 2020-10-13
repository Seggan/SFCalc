package io.github.seggan.sfcalc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.cscorelib2.chat.ChatColors;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.GitHubBuildsUpdater;

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
public class SFCalc extends JavaPlugin implements SlimefunAddon {

    private static SFCalc instance;

    protected final Set<RecipeType> blacklistedRecipes = new HashSet<>();
    protected final Set<String> blacklistedIds = new HashSet<>();
    protected final List<String> itemsSearched = new ArrayList<>();

    // "Localization"
    protected String headerString;
    protected String amountString;
    protected String neededString;
    protected String noItemString;
    protected String noNumberString;
    protected String tooManyCategoriesString;
    protected String tooManyItemsString;
    protected String notAPlayerString;

    @Override
    public void onEnable() {
        getLogger().info("SFCalc enabled.");

        saveDefaultConfig();

        // ConfigurationSection exceptionSection = config.getConfigurationSection("special-items");
        // Set<String> keys = exceptionSection.getKeys(false);
        // for (String key : keys) {
        // int amount = exceptionSection.getInt(key + ".amount");
        // String item = exceptionSection.getString(key + ".ingredient");
        // String[] ingredients = new String[amount];
        // for (int n = 0; n < amount; n++) {
        // ingredients[n] = item;
        // }
        // exceptions.put(key, ingredients);
        // }

        // init metrics
        new SFCalcMetrics(this);

        if (getConfig().getBoolean("options.auto-update") && getDescription().getVersion().startsWith("DEV - ")) {
            new GitHubBuildsUpdater(this, getFile(), "Seggan/SFCalc/master").start();
        }

        Objects.requireNonNull(getCommand("sfcalc")).setExecutor(new CalcExecutor(this));
        Objects.requireNonNull(getCommand("sfcalc")).setTabCompleter(new CalcCompleter());

        Objects.requireNonNull(getCommand("sfneeded")).setExecutor(new CalcExecutor(this));
        Objects.requireNonNull(getCommand("sfneeded")).setTabCompleter(new CalcCompleter());

        getServer().getPluginManager().registerEvents(new CalcHandler(), this);

        blacklistedRecipes.add(RecipeType.ORE_WASHER);
        blacklistedRecipes.add(RecipeType.GEO_MINER);
        blacklistedRecipes.add(RecipeType.GOLD_PAN);
        blacklistedRecipes.add(RecipeType.MOB_DROP);
        blacklistedRecipes.add(RecipeType.ORE_CRUSHER);
        blacklistedRecipes.add(RecipeType.NULL);

        blacklistedIds.add("uu_matter");
        blacklistedIds.add("silicon");

        instance = this;

        loadStrings();
    }

    @Override
    public void onDisable() {
        getLogger().info("SFCalc disabled.");
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/Seggan/SFCalc/issues";
    }

    static SFCalc getInstance() {
        return instance;
    }

    private void loadStrings() {
        FileConfiguration config = getConfig();
        headerString = ChatColors.color(config.getString("header-string") != null ? config.getString("header-string") : "&e&nRecipe for %s:");
        amountString = ChatColors.color(config.getString("amount-string") != null ? config.getString("amount-string") : "&e%d of %s");
        neededString = ChatColors.color(config.getString("needed-string") != null ? config.getString("needed-string") : "&e%d more %s needed");
        noItemString = ChatColors.color(config.getString("no-item-string") != null ? config.getString("no-item-string") : "&cThat item was not found.");
        noNumberString = ChatColors.color(config.getString("no-number-string") != null ? config.getString("no-number-string") : "&cThat's not a number!");
        tooManyCategoriesString = ChatColors.color(config.getString("category-error-string") != null ? config.getString("category-error-string") : "&cThat many categories is not supported yet. Please use the command form of the calculator.");
        tooManyItemsString = ChatColors.color(config.getString("item-error-string") != null ? config.getString("item-error-string") : "&cThat many items is not supported yet. Please use the command form of the calculator.");
        notAPlayerString = ChatColors.color(config.getString("not-a-player-string") != null ? config.getString("not-a-player-string") : "&cYou must be a player to send this message!");
    }
}
