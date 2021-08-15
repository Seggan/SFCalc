package io.github.seggan.sfcalc;

import io.github.mooy1.infinitylib.AbstractAddon;
import io.github.mooy1.infinitylib.bstats.bukkit.Metrics;
import io.github.mooy1.infinitylib.commands.AbstractCommand;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Getter
public class SFCalc extends AbstractAddon implements Listener {

    private static SFCalc instance;
    private Calculator calculator;

    private final Set<RecipeType> blacklistedRecipes = new HashSet<>();
    private final Set<String> blacklistedIds = new HashSet<>();

    private StringRegistry stringRegistry;

    @Override
    protected void enable() {
        instance = this;

        stringRegistry = new StringRegistry(getConfig());
        calculator = new Calculator(this);

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
        blacklistedIds.add("RUBBER");
        blacklistedIds.add("VOID_BIT");
    }

    @Override
    protected void disable() {
        instance = null;
    }

    @Nonnull
    @Override
    protected String getGithubPath() {
        return "Seggan/SFCalc/master";
    }

    @Nullable
    @Override
    public String getAutoUpdatePath() {
        return "auto-updates";
    }

    @Nullable
    @Override
    protected Metrics setupMetrics() {
        return new SFCalcMetrics(this);
    }

    @Nullable
    @Override
    protected List<AbstractCommand> setupSubCommands() {
        return Arrays.asList(new CalcCommand(this), new NeededCommand(this));
    }

    @Nonnull
    static SFCalc inst() {
        return instance;
    }

    public Calculator getCalc() {
        return calculator;
    }

}
