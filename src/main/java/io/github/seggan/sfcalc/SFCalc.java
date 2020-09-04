package io.github.seggan.sfcalc;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SFCalc extends JavaPlugin implements SlimefunAddon {
    FileConfiguration config = getConfig();

    final Set<RecipeType> blacklistedRecipes = new HashSet<>();
    final Set<String> blacklistedIds = new HashSet<>();

    final String headerString = config.getString("header-string");
    final String amountString = config.getString("amount-string");



    @Override
    public void onEnable() {
        getLogger().info("SFCalc enabled.");

        saveDefaultConfig();

        Objects.requireNonNull(getCommand("sfcalc")).setExecutor(new Executor(this));
        Objects.requireNonNull(getCommand("sfcalc")).setTabCompleter(new Completer());

        Objects.requireNonNull(getCommand("sfneeded")).setExecutor(new Executor(this));
        Objects.requireNonNull(getCommand("sfneeded")).setTabCompleter(new Completer());

        blacklistedRecipes.add(RecipeType.ORE_WASHER);
        blacklistedRecipes.add(RecipeType.GEO_MINER);
        blacklistedRecipes.add(RecipeType.GOLD_PAN);
        blacklistedRecipes.add(RecipeType.MOB_DROP);
        blacklistedRecipes.add(RecipeType.ORE_CRUSHER);
        blacklistedRecipes.add(RecipeType.NULL);

        blacklistedIds.add("uu_matter");
        blacklistedIds.add("silicon");
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
}
