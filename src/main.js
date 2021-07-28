$('.calculator').on('submit', function() {
    $.getJSON("https://raw.githubusercontent.com/Seggan/SFCalc/gh-pages/src/items.json", function(items) {
        alert(items[0].id);
    });
});