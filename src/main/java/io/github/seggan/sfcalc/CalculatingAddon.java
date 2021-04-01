package io.github.seggan.sfcalc;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;

import java.util.Set;

/**
 * Have your main addon class implement this interface to use the {@link Calculator}
 *
 * @author Seggan
 */
public interface CalculatingAddon {

    Set<RecipeType> getBlacklistedRecipes();
    Set<String> getBlacklistedIds();
}
