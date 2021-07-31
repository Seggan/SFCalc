var items = {};

fetch('https://raw.githubusercontent.com/Seggan/SFCalc/gh-pages/src/items.json')
.then(res => res.json())
.then(itemList => {
    for (const item of itemList) {
        items[item.id] = item;
    }
}).catch(err => console.error);

function calculate(state, item) {

}

window.onload = e => {
    document.getElementById('calculator').onsubmit = e => {
        alert(items['OSMIUM_DUST'].id);

        return false;
    };
};