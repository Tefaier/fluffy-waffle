const url = 'http://localhost:5432/api';

function getAllLots() {
    fetch(url + '/lot')
        .then(response => response.json())
        .then(data => {
            data.forEach(lot => {
                const card = document.createElement('div');
                card.className = 'card';

                card.innerHTML = `
                    <div class="card__gallery">
                        <div class="card__control card__control_position_left">◄</div>
                        <img class="card__image" src="${lot.image}" alt="Picture">
                        <div class="card__control card__control_position_right">►</div>
                    </div>
                    <h3 class="card__name">${lot.name}</h3>
                    <h4 class="card__owner">${lot.owner}</h4>
                    <p class="card__price">${lot.price}</p>
                    <p class="card__time">${lot.time}</p>
                    <p class="card__status card__status_last">${lot.status}</p>
                `;

                document.body.appendChild(card);
            });
        })
        .catch(error => console.error('Ошибка при получении данных о лотах:', error));
}