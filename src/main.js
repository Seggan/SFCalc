function getJSON(url){
    var req = new XMLHttpRequest();
    req.open("GET", url, false);
    req.send(null);
    return JSON.parse(req.responseText);          
}

document.onload = function() {
    document.getElementById('calculator').onsubmit = function() {
        var items = getJSON('https://raw.githubusercontent.com/Seggan/SFCalc/gh-pages/src/items.json');
        alert(items[0].name);
    };
};