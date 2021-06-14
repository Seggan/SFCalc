package io.github.seggan.sfcalc;

import me.mrCookieSlime.Slimefun.cscorelib2.chat.ChatColors;
import org.apache.commons.lang.Validate;

import lombok.Getter;

import java.util.regex.Pattern;
import javax.annotation.Nonnull;

@Getter
public final class StringRegistry {

    private static final Pattern FORMAT_PATTERN = Pattern.compile("%\\d");

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

    StringRegistry() {
        headerString = SFCalc.inst().getConfig().getString("header-string", "&e&nRecipe for %s:");
        headerAmountString = SFCalc.inst().getConfig().getString("header-amount-string", "&e&nRecipe for %d %s:");
        stackString = SFCalc.inst().getConfig().getString("stack-string", "&e%d (%d x%d + %d)");
        amountString = SFCalc.inst().getConfig().getString("amount-string", "&e%s of %s");
        neededString = SFCalc.inst().getConfig().getString("needed-string", "&e%s more %s needed");
        noItemString = SFCalc.inst().getConfig().getString("no-item-string", "&cThat item was not found.");
        notANumberString = SFCalc.inst().getConfig().getString("not-a-number-string", "&cThat's not a number!");
        tooManyCategoriesString = SFCalc.inst().getConfig().getString("category-error-string", "&cThat many categories is not supported yet. Please use the command form of the calculator.");
        tooManyItemsString = SFCalc.inst().getConfig().getString("item-error-string", "&cThat many items is not supported yet. Please use the command form of the calculator.");
        notAPlayerString = SFCalc.inst().getConfig().getString("not-a-player-string", "&cYou must be a player to send this message!");
        invalidNumberString = SFCalc.inst().getConfig().getString("invalid-number-string", "&cInvalid number!");
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
