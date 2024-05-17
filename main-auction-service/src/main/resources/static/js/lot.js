const url = '/api';
const urlParams = new URLSearchParams(window.location.search);
const lotId = urlParams.get('id');

function load() {
    fetch(url + '/lot/' + lotId)
        .then(response => response.json())
        .then(data => {
            const currentGalleryImage =  document.querySelector('.description__image');
            currentGalleryImage.src = data.images[0];

            const descriptionTitle = document.querySelector('.description__title');
            // title is not currently present
            descriptionTitle.innerHTML = data.name;

            const descriptionText = document.querySelector('.description__text');
            descriptionText.innerHTML = data.description;

            const currentPrice = document.querySelector('.purchase__price-value');
            currentPrice.innerHTML = getCurrentPrice(data.lotBets);
        })
        .catch(error => console.error('Error while fetching data about lot:', error));
}

function getCurrentPrice(bets) {
    return bets.reduce((max, currentBet) => {
        return betValue(currentBet) > betValue(max) ? currentBet : max;
    }, bets[0]);
}

function betValue(bet) {
    return bet.value.integerPart + bet.value.decimalPart / Math.pow(10, bet.value.decimalPart.toString().length);
}

document.addEventListener('DOMContentLoaded', function() {
    load();
});