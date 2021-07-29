var itemObj;
fetch('https://raw.githubusercontent.com/Seggan/SFCalc/gh-pages/src/items.json')
.then(res => res.json())
.then(out => itemObj = out)
.catch(err => console.error);

window.onload = e => {
    document.getElementById('calculator').onsubmit = e => {
        alert(itemObj[0].id);

        return false;
    };
};