var getJSON = function(url, callback) {
    fetch(url)
    .then(res => res.json())
    .then(out => {
        callback(out);
    })
    .catch(err => console.error);
};

window.onload = e => {
    document.getElementById('calculator').onsubmit = e => {
        getJSON('https://raw.githubusercontent.com/Seggan/SFCalc/gh-pages/src/items.json', items => {
            alert(items[0].name);
        });

        return false;
    };
};