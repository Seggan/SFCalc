package io.github.seggan.sfcalc;

import io.github.mooy1.infinitylib.commands.CommandManager;
import io.github.mooy1.infinitylib.core.PluginUtils;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

@Getter
public class SFCalc extends JavaPlugin implements SlimefunAddon, Listener {
    private static SFCalc instance;
    private Calculator calculator;

    private final Set<RecipeType> blacklistedRecipes = new HashSet<>();
    private final Set<String> blacklistedIds = new HashSet<>();

    private StringRegistry stringRegistry;

    @Override
    public void onEnable() {
        instance = this;
        calculator = new Calculator(this);

        PluginUtils.setup("SFCalc", this, "Seggan/SFCalc/master", getFile());
        PluginUtils.setupMetrics(8812);

        CommandManager.setup("sfcalc", "/sfc", new CalcCommand(), new NeededCommand());

        stringRegistry = new StringRegistry();

        blacklistedRecipes.add(RecipeType.ORE_WASHER);
        blacklistedRecipes.add(RecipeType.GEO_MINER);
        blacklistedRecipes.add(RecipeType.GOLD_PAN);
        blacklistedRecipes.add(RecipeType.MOB_DROP);
        blacklistedRecipes.add(RecipeType.BARTER_DROP);
        blacklistedRecipes.add(RecipeType.ORE_CRUSHER);
        blacklistedRecipes.add(RecipeType.NULL);

        blacklistedIds.add("UU_MATTER");
        blacklistedIds.add("SILICON");
        blacklistedIds.add("FALLEN_METEOR");

    }

    @Nonnull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    public Set<RecipeType> getBlacklistedRecipes() {
        return blacklistedRecipes;
    }
    public Set<String> getBlacklistedIds() {
        return blacklistedIds;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/Seggan/SFCalc/issues";
    }

    @Nonnull
    static SFCalc inst() {
        return instance;
    }

    public Calculator getCalc() {
        return calculator;
    }

    @EventHandler
    public void onOpJoin(PlayerJoinEvent e) {
        if (e.getPlayer().isOp() && stringRegistry.getAmountString().contains("%s")) {
            e.getPlayer().sendMessage(ChatColor.RED + "[SFCalc] Hey, I see you are using outdated SFCalc config! For SFCalc to work properly, please delete config.yml and restart the server");
        }
    }
}
