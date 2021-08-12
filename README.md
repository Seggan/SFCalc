# SFCalc
A calculator for the plugin Slimefun. This is based off of john000708's SlimeCalculator.

Find most recent build [here](https://thebusybiscuit.github.io/builds/Seggan/SFCalc/master).

## Usage

This addon for Slimefun adds a new command: /sfcalc. This command can calculate how much basic slimefun resources you need to make the specified item.

Usage: `/sfcalc calc [required: item] [optional: amount]`

Item is the id of the item you want to craft. An item id is like this: "electric_motor", "carbonado", "solar_generator" (basic solar generator), etc. For tiered machines that are not lowest tier, add the tier number after the id. For example: "solar_generator_3" (carbonado solar generator), "lava_generator_2" (advanced lava generator), "carbon_press" (carbon press [tier 1]), etc.

Amount is the number of items you want to craft. It defaults to 1 if you don't specify any amount.

Using `/sfcalc needed` instead of `/sfcalc calc` will show the items needed for the calculated item.

Minecraft version: 1.15-1.17

Slimefun version: RC-20 or newer