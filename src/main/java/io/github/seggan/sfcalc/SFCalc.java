package io.github.seggan.sfcalc;

import io.github.mooy1.infinitylib.commands.CommandManager;
import io.github.mooy1.infinitylib.core.PluginUtils;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

@Getter
public class SFCalc extends JavaPlugin implements SlimefunAddon, CalculatingAddon {

    private static SFCalc instance;

    private final Set<RecipeType> blacklistedRecipes = new HashSet<>();
    private final Set<String> blacklistedIds = new HashSet<>();

    private StringRegistry stringRegistry;

    @Override
    public void onEnable() {
        instance = this;

        PluginUtils.setup("SFCalc", this, "Seggan/SFCalc/master", getFile());
        // init metrics
        new SFCalcMetrics(this);

        CommandManager.setup("sfcalc", "/sfc", new CalcCommand());

        stringRegistry = new StringRegistry();

        blacklistedRecipes.add(RecipeType.ORE_WASHER);
        blacklistedRecipes.add(RecipeType.GEO_MINER);
        blacklistedRecipes.add(RecipeType.GOLD_PAN);
        blacklistedRecipes.add(RecipeType.MOB_DROP);
        blacklistedRecipes.add(RecipeType.BARTER_DROP);
        blacklistedRecipes.add(RecipeType.ORE_CRUSHER);
        blacklistedRecipes.add(RecipeType.NULL);

        blacklistedIds.add("uu_matter");
        blacklistedIds.add("silicon");
        blacklistedIds.add("fallen_meteor");
    }

    @Nonnull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/Seggan/SFCalc/issues";
    }

    @Nonnull
    static SFCalc inst() {
        return instance;
    }
}
