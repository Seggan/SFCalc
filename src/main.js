$(function() {
    $.getJSON("https://raw.githubusercontent.com/Seggan/SFCalc/gh-pages/src/items.json", function(data) {
        alert(data[0].name);
    });
});