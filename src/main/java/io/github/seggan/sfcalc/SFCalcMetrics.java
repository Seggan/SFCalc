package io.github.seggan.sfcalc;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bstats.bukkit.Metrics;

/*
 * Copyright (C) 2020 Seggan
 * Email: segganew@gmail.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
public class SFCalcMetrics extends Metrics {

    private boolean sent = false;

    public SFCalcMetrics(SFCalc plugin) {
        super(plugin, 8812);

        addCustomChart(new Metrics.AdvancedPie("items_searched", () -> {
            Map<String, Integer> result = new HashMap<>();
            Set<String> itemSet = new HashSet<>(plugin.itemsSearched);

            for (String item : itemSet) {
                result.put(item, Collections.frequency(plugin.itemsSearched, item));
            }

            if (sent) {
                sent = false;
                plugin.itemsSearched.clear();
            } else {
                sent = true;
            }

            return result;
        }));

        addCustomChart(new Metrics.SingleLineChart("searches", () -> {
            int searches = plugin.itemsSearched.size();

            if (sent) {
                sent = false;
                plugin.itemsSearched.clear();
            } else {
                sent = true;
            }

            return searches;
        }));
    }

}
