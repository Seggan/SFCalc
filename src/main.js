var items = {};
const blacklistedItems = [
    "UU_MATTER",
    "SILICON"
];
const blacklistedRecipes = [
    "ore_washer",
    "geo_miner",
    "gold_pan",
    "mob_drop",
    "barter_drop",
    "ore_crusher",
    "multiblock"
];

fetch('https://raw.githubusercontent.com/Seggan/SFCalc/gh-pages/src/items.json')
.then(res => res.json())
.then(itemList => {
    for (const item of itemList) {
        items[item.id] = item;
    }
}).catch(err => console.error);

function add(map1, map2) {
    for (const key in map2) {
        if (key in map1) {
            var inThere = map1[key];
            inThere += map2[key];
            map1[key] = inThere;
        } else {
            map1[key] = map2[key];
        }
    }
}

function calculate(itemStr) {
    var results = {};
    var item = items[itemStr];
    for (const ing of item.recipe) {
        var value = ing.value;
        var ingItem = items[value];
        if (!ing.slimefun || blacklistedItems.contains(value) || blacklistedRecipes.contains(ingItem.recipeType)) {
            var temp = {};
            temp[value] = 1;
            add(results, temp);
        } else {
            var ret = calculate(value);
            add(results, ret);
        }
    }

    return results;
}

window.onload = e => {
    document.getElementById('submit').onclick = e => {
        console.log(calculate(document.getElementById('id').value));

        return false;
    };
};