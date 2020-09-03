package io.github.seggan.sfcalc;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;

public class SFCalc extends JavaPlugin implements SlimefunAddon {
    public static ArrayList<RecipeType> blacklistedRecipes = new ArrayList<>();

    public static SFCalc instance;

    @Override
    public void onEnable() {
        getLogger().info("SFCalc enabled.");

        Objects.requireNonNull(getCommand("sfcalc")).setExecutor(new Executor(this));

        blacklistedRecipes.add(RecipeType.ORE_WASHER);
        blacklistedRecipes.add(RecipeType.GEO_MINER);
        blacklistedRecipes.add(RecipeType.GOLD_PAN);
        blacklistedRecipes.add(RecipeType.MOB_DROP);
        blacklistedRecipes.add(RecipeType.ORE_CRUSHER);

        instance = this;
    }

    @Override
    public void onDisable() {
        getLogger().info("SFCalc disabled.");
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    public static SFCalc getInstance() {
        return instance;
    }

    @Override
    public String getBugTrackerURL() {
        return null;
    }
}
