const url = 'http://localhost:8080/api';

function getAllLots() {
    fetch(url + '/lot',  { method: 'GET'
    })
        .then(response => response.json())
        .then(async data => {
            const cardsContainer = document.querySelector('.cards');
            for (const lot of data) {
                const card = document.createElement('a');
                card.className = 'card';
                card.href = `/lot?id=${lot.id}`;

                let username = await getUserName(lot);

                card.innerHTML = `
                    <div class="card__gallery">
                        <div class="card__control card__control_position_left">◄</div>
                        <img class="card__image" src="${lot.images[0]}" alt="Picture">
                        <div class="card__control card__control_position_right">►</div>
                    </div>
                    <h3 class="card__name">${lot.name}</h3>
                    <h4 class="card__owner">${username}</h4>
                    <p class="card__price">${getCurrency(lot.initialPrice) + getCurrentPrice(lot.lotBets, lot.initialPrice)}</p>
                    <p class="card__time">${formatDate(lot.finishTime)}</p>
                `;

                cardsContainer.appendChild(card);
            }
        })
        .catch(error => console.error('Error while fetching data about lots:', error));
}

document.addEventListener('DOMContentLoaded', function() {
    getAllLots();
});

function getUserName(lot) {
    let userId = lot.userId
    return fetch(`http://localhost:8080/api/user/name/${userId}`, { method: 'GET'})
        .then(response => response.text())
        .then(data=> {
        return data;
    });
}

function formatDate(dateString) {
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    return `${day}.${month}.${year}`;
}