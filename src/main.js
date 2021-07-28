$('.calculator').on('submit', function() {
    var items;
    $.getJSON("https://gitcdn.link/repo/Seggan/SFCalc/gh-pages/src/items.json", function(data) {
        items = data;
    });

    alert(items[0].id);
});