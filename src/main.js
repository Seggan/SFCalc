var getJSON = function(url, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.responseType = 'json';
    xhr.onload = function() {
        alert('code');
        var status = xhr.status;
        if (status === 200) {
            callback(xhr.response);
        } else {
            console.error(status);
        }
    };
    xhr.send();
};

window.onload = e => {
    document.getElementById('calculator').onsubmit = e => {
        getJSON('https://raw.githubusercontent.com/Seggan/SFCalc/gh-pages/src/items.json', items => {
            alert(items[0].name);
        });
    };
};