package io.github.seggan.sfcalc;

import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SingleLineChart;

import java.util.*;

public class SFCalcMetrics extends Metrics {

    private boolean sent = false;

    private static final List<String> itemsSearched = new ArrayList<>();

    public SFCalcMetrics(SFCalc plugin) {
        super(plugin, 8812);

        addCustomChart(new AdvancedPie("items_searched", () -> {
            Map<String, Integer> result = new HashMap<>();
            Set<String> itemSet = new HashSet<>(itemsSearched);

            for (String item : itemSet) {
                result.put(item, Collections.frequency(itemsSearched, item));
            }

            if (sent) {
                sent = false;
                itemsSearched.clear();
            } else {
                sent = true;
            }

            return result;
        }));

        addCustomChart(new SingleLineChart("searches", () -> {
            int searches = itemsSearched.size();

            if (sent) {
                sent = false;
                itemsSearched.clear();
            } else {
                sent = true;
            }

            return searches;
        }));
    }

    public static void addItemSearched(String s) {
        itemsSearched.add(ChatUtils.removeColorCodes(s));
    }
}
