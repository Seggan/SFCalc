$('.calculator').on('submit', function() {
    $.getJSON("https://gitcdn.link/repo/Seggan/SFCalc/gh-pages/src/items.json", function(items) {
        alert(items[0].id);
    });
});