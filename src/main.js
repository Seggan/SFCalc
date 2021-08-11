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
    "ore_crusher"
];

fetch('https://raw.githubusercontent.com/Seggan/SFCalc/gh-pages/src/items.json')
.then(res => res.json())
.then(itemList => {
    for (const item of itemList) {
        items[item.id] = item;
    }
}).catch(err => console.error);

function calculate(item) {
    var results = {};
    for (const ing of item.recipe) {
        var value = ing.value;
        if (ing.slimefun) {
            var ret = calculate(value);
            for (const key in ret) {
                if (key in results) {
                    var inThere = results[key];
                    inThere += ret[key];
                    results[key] = inThere;
                } else {
                    results[key] = ret[key];
                }
            }
        } else {
            if (value in results) {
                var inThere = results[value];
                inThere++;
                results[value] = inThere;
            } else {
                results[value] = 1;
            }
        }
    }

    return results;
}

window.onload = e => {
    document.getElementById('calculator').onsubmit = e => {
        alert(calculate(document.getElementById('id').value));

        return false;
    };
};