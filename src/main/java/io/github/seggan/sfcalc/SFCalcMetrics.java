package io.github.seggan.sfcalc;

import io.github.mooy1.infinitylib.bstats.bukkit.Metrics;
import io.github.mooy1.infinitylib.bstats.charts.AdvancedPie;
import io.github.mooy1.infinitylib.bstats.charts.SingleLineChart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        itemsSearched.add(s);
    }
}
