const url = '/api';

function getAllLots() {
    fetch(url + '/lot',  { method: 'GET'
    })
        .then(response => response.json())
        .then(data => {
            const cardsContainer = document.querySelector('.cards');
            data.forEach(lot => {
                const card = document.createElement('a');
                card.className = 'card';
                card.href = `/lot${lot.id}`;

                card.innerHTML = `
                    <div class="card__gallery">
                        <div class="card__control card__control_position_left">◄</div>
                        <img class="card__image" src="${lot.images[0]}" alt="Picture">
                        <div class="card__control card__control_position_right">►</div>
                    </div>
                    <h3 class="card__name">${lot.name}</h3>
                    <h4 class="card__owner">${lot.user.name}</h4>
                    <p class="card__price">${betValue(getCurrentPrice(lot.lotBets))}</p>
                    <p class="card__time">${lot.time}</p>
                    <p class="card__status card__status_last">${lot.status}</p>
                `;

                cardsContainer.appendChild(card);
            });
        })
        .catch(error => console.error('Error while fetching data about lots:', error));
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
    getAllLots();
});
