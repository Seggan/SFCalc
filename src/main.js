var getJSON = function(url, callback) {
    alert('code')
    fetch(url)
    .then(res => {
        alert('codd');
        res.json()
    })
    .then(out => {
        alert('codde');
        callback(out);
    })
    .catch(err => console.error(err));
};

window.onload = e => {
    document.getElementById('calculator').onsubmit = e => {
        getJSON('https://raw.githubusercontent.com/Seggan/SFCalc/gh-pages/src/items.json', items => {
            alert(items[0].name);
        });

        return false;
    };
};