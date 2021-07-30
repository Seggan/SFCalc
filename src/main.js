window.onload = e => {
    fetch('https://raw.githubusercontent.com/Seggan/SFCalc/gh-pages/src/items.json')
    .then(res => res.json())
    .then(itemList => {
        var items = {};
        for (const key in itemList) {
            console.log(key);
            var item = itemList[key];
            console.log(item);
            items[item.id] = item;
        }
    
        document.getElementById('calculator').onsubmit = e => {
            alert(itemList[0].id);
    
            return false;
        };
    })
    .catch(err => console.error);    
};