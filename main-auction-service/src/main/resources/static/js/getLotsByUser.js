const url = 'http://localhost:8080/api';

async function getLotsByUser() {
    let userId = await getUserId();

    fetch(url + '/user/' + userId + "/lots",  { method: 'GET'
        })
        .then(response => response.json())
        .then(async data => {
            let ownsAwaits = data.ownsAwaits;
            let ownsInProgress = data.ownsInProgress;
            let ownsAwaitsPayment = data.ownsAwaitsPayment;

            const cardsContainer = document.querySelector('.cards');

            for (const lot of ownsAwaits) {
                cardsInfo[lot.id] = {
                    currentIndex: 0,
                    imageUrls: lot.images
                };
                let username = await getUserName(lot.userId);
                const card = document.createElement('a');
                card.className = 'card';
                card.href=`/lot?id=${lot.id}`;
                card.id = lot.id;
                card.innerHTML = `
                    <div class="card__gallery">
                        <div class="card__control card__control_position_left">◄</div>
                        <img class="card__image" src="${lot.images[0]}" alt="Picture">
                        <div class="card__control card__control_position_right">►</div>
                    </div>
                    <h3 class="card__name">${lot.name}</h3>
                    <h4 class="card__owner">${username}</h4>
                    <p class="card__price">${await getLotValue(lot.id)}</p>
                    <p class="card__time">${formatDate(lot.finishTime)}</p>
                    <p class="card__status card__status_beaten">Awaiting the start of trading</p>
                    `;

                    const leftControl = card.querySelector('.card__control_position_left');
                    const rightControl = card.querySelector('.card__control_position_right');
                    leftControl.addEventListener('click', () => {
                        updatedImage(lot.id, -1);
                    });
                    rightControl.addEventListener('click', () => {
                        updatedImage(lot.id, +1);
                    });

                    cardsContainer.appendChild(card);
            }

            for (const lot of ownsInProgress) {
                cardsInfo[lot.id] = {
                    currentIndex: 0,
                    imageUrls: lot.images
                };
                let username = await getUserName(lot.userId);
                const card = document.createElement('a');
                card.className = 'card';
                card.href=`/lot?id=${lot.id}`;
                card.id = lot.id;
                card.innerHTML = `
                    <div class="card__gallery">
                        <div class="card__control card__control_position_left">◄</div>
                        <img class="card__image" src="${lot.images[0]}" alt="Picture">
                        <div class="card__control card__control_position_right">►</div>
                    </div>
                    <h3 class="card__name">${lot.name}</h3>
                    <h4 class="card__owner">${username}</h4>
                    <p class="card__price">${await getLotValue(lot.id)}</p>
                    <p class="card__time">${formatDate(lot.finishTime)}</p>
                    <p class="card__status card__status_last">Bidding process</p>
                    `;

                    const leftControl = card.querySelector('.card__control_position_left');
                    const rightControl = card.querySelector('.card__control_position_right');
                    leftControl.addEventListener('click', () => {
                        updatedImage(lot.id, -1);
                    });
                    rightControl.addEventListener('click', () => {
                        updatedImage(lot.id, +1);
                    });

                    cardsContainer.appendChild(card);
            }
            for (const lot of ownsAwaitsPayment) {
                cardsInfo[lot.id] = {
                    currentIndex: 0,
                    imageUrls: lot.images
                };
                let username = await getUserName(lot.userId);
                const card = document.createElement('a');
                card.className = 'card';
                card.href=`/lot?id=${lot.id}`;
                card.id = lot.id;
                card.innerHTML = `
                    <div class="card__gallery">
                        <div class="card__control card__control_position_left">◄</div>
                        <img class="card__image" src="${lot.images[0]}" alt="Picture">
                        <div class="card__control card__control_position_right">►</div>
                    </div>
                    <h3 class="card__name">${lot.name}</h3>
                    <h4 class="card__owner">${username}</h4>
                    <p class="card__price">${await getLotValue(lot.id)}</p>
                    <p class="card__time">${formatDate(lot.finishTime)}</p>
                    <p class="card__status card__status_payment">Awaits Payment</p>
                    `;

                    const leftControl = card.querySelector('.card__control_position_left');
                    const rightControl = card.querySelector('.card__control_position_right');
                    leftControl.addEventListener('click', () => {
                        updatedImage(lot.id, -1);
                    });
                    rightControl.addEventListener('click', () => {
                        updatedImage(lot.id, +1);
                    });

                    cardsContainer.appendChild(card);
            }
        })
        .catch(error => console.error('Error while fetching data about lots:', error));
}

async function setTitle() {
    let userId = await getUserId();
    const title = document.querySelector('.header__title');
    title.innerText = await getUserName(userId);
}

document.addEventListener('DOMContentLoaded', function() {
    setTitle();
    getLotsByUser();
});

function getUserName(userId) {
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

async function getLotValue(lotId) {
    let currency;
    let integerPart;
    let decimalPart;
    let value;

    const response = await fetch(url + '/lot/' + lotId + '/top');
    const data = await response.json();

    currency = getCurrency(data);
    console.log(data);
    integerPart = data.integerPart;
    decimalPart = data.decimalPart;
    value = currency + integerPart + "." + decimalPart;
    console.log(currency + integerPart + "." + decimalPart);

    return value;
}