package io.github.seggan.sfcalc;

import java.io.IOException;

import javax.annotation.Nonnull;

import lombok.Getter;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.mooy1.infinitylib.configuration.AddonConfig;
import me.mrCookieSlime.Slimefun.cscorelib2.chat.ChatColors;

@Getter
public final class StringRegistry {

    private final String headerString;
    private final String headerAmountString;
    private final String stackString;
    private final String amountString;
    private final String neededString;
    private final String noItemString;
    private final String notANumberString;
    private final String tooManyCategoriesString;
    private final String tooManyItemsString;
    private final String notAPlayerString;
    private final String invalidNumberString;

    StringRegistry(AddonConfig config) {
        if (config.getString("header-string").contains("%s")) {
            // The config is outdated, overwrite it with default values
            config.resetToDefaults();
        }

        headerString = config.getString("header-string");
        headerAmountString = config.getString("header-amount-string");
        stackString = config.getString("stack-string");
        amountString = config.getString("amount-string");
        neededString = config.getString("needed-string");
        noItemString = config.getString("no-item-string");
        notANumberString = config.getString("not-a-number-string");
        tooManyCategoriesString = config.getString("category-error-string");
        tooManyItemsString = config.getString("item-error-string");
        notAPlayerString = config.getString("not-a-player-string");
        invalidNumberString = config.getString("invalid-number-string");
    }

    @Nonnull
    public static String format(@Nonnull String formatString, @Nonnull Object... objects) {
        Validate.notNull(formatString);
        Validate.noNullElements(objects);

        String finalString = formatString;

        for (int i = 0; i < objects.length; i++) {
            finalString = finalString.replace("%" + (i + 1), objects[i].toString());
        }

        return ChatColors.color(finalString);
    }

}
