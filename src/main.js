const items = {};
const blacklistedItems = [
    "UU_MATTER",
    "SILICON",
    "RUBBER",
    "VOID_BIT"
];
const blacklistedRecipes = [
    "ore_washer",
    "geo_miner",
    "gold_pan",
    "mob_drop",
    "barter_drop",
    "ore_crusher",
    "multiblock",
    "meteor_attractor"
];

fetch('https://raw.githubusercontent.com/Seggan/SFCalc/gh-pages/src/items.json')
.then(res => res.json())
.then(itemList => {
    for (const item of itemList) {
        items[item.id] = item;
    }
}).catch(_err => console.error);

function add(map1, map2, amount) {
    for (const key in map2) {
        if (key in map1) {
            let inThere = map1[key];
            inThere += map2[key] * amount;
            map1[key] = inThere;
        } else {
            map1[key] = map2[key] * amount;
        }
    }
}

function calculate(itemStr) {
    const results = {};
    const item = items[itemStr];
    for (const ing of item.recipe) {
        const value = ing.value;
        const ingItem = items[value];
        if (ing.value.toUpperCase() != ing.value || blacklistedItems.includes(value) || blacklistedRecipes.includes(ingItem.recipeType)) {
            const temp = {};
            temp[value] = 1;
            add(results, temp, ing.amount);
        } else {
            const ret = calculate(value);
            add(results, ret, ing.amount);
        }
    }

    return results;
}

window.onload = _e => {
    document.getElementById('calculator').onsubmit = _e => {
        const id = document.getElementById('id').value.toUpperCase();
        if (!(id in items)) {
            alert('Invalid item ID');
            return false;
        }

        let results = calculate(id);

        results = Object.fromEntries(Object.entries(results).sort(([,a],[,b]) => a-b));

        document.getElementById('result-table').removeAttribute('hidden');

        const div = document.getElementById('results');
        div.innerHTML = '';
        for (const result in results) {
            let color = '_0';
            let name = result;

            if (name.toUpperCase() === name) {
                name = items[result].name;
            }

            if (name.startsWith('§')) {
                color = '_' + name.charAt(1);
                name = name.substring(2);
            }

            const disp = document.createElement('tr');
            disp.innerHTML = '<td class="' + color + '\"><b>' + name + '</b></td><td>' + results[result] * document.getElementById('amount').value + '</td>';

            div.appendChild(disp);
        }

        return false;
    };
};