package io.github.seggan.sfcalc;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import lombok.Getter;

import org.apache.commons.lang.Validate;

import io.github.mooy1.infinitylib.configuration.AddonConfig;
import me.mrCookieSlime.Slimefun.cscorelib2.chat.ChatColors;

@Getter
public final class StringRegistry {

    private final Pattern percentPattern = Pattern.compile("(%s|%d)");
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
        headerString = reformat(config, "header-string", 1);
        headerAmountString = reformat(config, "header-amount-string", 2, 1);
        stackString = reformat(config, "stack-string", 1, 2, 3, 4);
        amountString = reformat(config, "amount-string", 2, 1);
        neededString = reformat(config, "needed-string", 2, 1);
        noItemString = config.getString("no-item-string");
        notANumberString = config.getString("not-a-number-string");
        tooManyCategoriesString = config.getString("category-error-string");
        tooManyItemsString = config.getString("item-error-string");
        notAPlayerString = config.getString("not-a-player-string");
        invalidNumberString = config.getString("invalid-number-string");

        config.save();
    }

    private String reformat(AddonConfig config, String key, int... numbers) {
        String val = config.getString(key);

        AtomicInteger i = new AtomicInteger();
        String formatted = this.percentPattern.matcher(val)
                .replaceAll(matchResult -> "%" + numbers[i.getAndIncrement()]);

        if (i.get() == numbers.length) {
            config.set(key, formatted);
            return formatted;
        }

        return val;
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
