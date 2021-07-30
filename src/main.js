var itemList;
fetch('https://raw.githubusercontent.com/Seggan/SFCalc/gh-pages/src/items.json')
.then(res => res.json())
.then(out => itemList = out)
.catch(err => console.error);

var items = {};

for (const key in itemList) {
    console.log(key);
    var item = itemList[key];
    console.log(item);
    items[item.id] = item;
}

console.log(items);

window.onload = e => {
    document.getElementById('calculator').onsubmit = e => {
        alert(itemList[0].id);

        return false;
    };
};