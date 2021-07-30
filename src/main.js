var itemList;
fetch('https://raw.githubusercontent.com/Seggan/SFCalc/gh-pages/src/items.json')
.then(res => res.json())
.then(out => itemList = out)
.catch(err => console.error);

var items = {};

for (const item of itemList) {
    items[item.id] = item;
}

console.log(items["OSMIUM_DUST"]);

window.onload = e => {
    document.getElementById('calculator').onsubmit = e => {
        alert(itemList[0].id);

        return false;
    };
};