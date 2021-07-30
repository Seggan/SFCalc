var items = {};

fetch('https://raw.githubusercontent.com/Seggan/SFCalc/gh-pages/src/items.json')
.then(res => res.json())
.then(itemList => {
    for (const key in itemList) {
        var item = itemList[key];
        items[item.id] = item;
    }
}).catch(err => console.error);  

window.onload = e => {
    document.getElementById('calculator').onsubmit = e => {
        alert(items['OSMIUM_DUST'].id);

        return false;
    };
};