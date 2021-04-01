package io.github.seggan.sfcalc;

import io.github.mooy1.infinitylib.core.ConfigUtils;
import lombok.Getter;
import org.apache.commons.lang.Validate;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

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
        headerString = ConfigUtils.getString("header-string", "&e&nRecipe for %s:");
        headerAmountString = ConfigUtils.getString("header-amount-string", "&e&nRecipe for %d %s:");
        stackString = ConfigUtils.getString("stack-string", "&e%d (%d x%d + %d)");
        amountString = ConfigUtils.getString("amount-string", "&e%s of %s");
        neededString = ConfigUtils.getString("needed-string", "&e%s more %s needed");
        noItemString = ConfigUtils.getString("no-item-string", "&cThat item was not found.");
        notANumberString = ConfigUtils.getString("not-a-number-string", "&cThat's not a number!");
        tooManyCategoriesString = ConfigUtils.getString("category-error-string", "&cThat many categories is not supported yet. Please use the command form of the calculator.");
        tooManyItemsString = ConfigUtils.getString("item-error-string", "&cThat many items is not supported yet. Please use the command form of the calculator.");
        notAPlayerString = ConfigUtils.getString("not-a-player-string", "&cYou must be a player to send this message!");
        invalidNumberString = ConfigUtils.getString("invalid-number-string", "&cInvalid number!");
    }

    @Nonnull
    public static String format(@Nonnull String formatString, @Nonnull Object... objects) {
        Validate.notNull(formatString);
        Validate.noNullElements(objects);

        String finalString = formatString;

        for (int i = 0; i < objects.length; i++) {
            finalString = finalString.replace("%" + (i + 1), objects[i].toString());
        }

        return finalString;
    }
}
