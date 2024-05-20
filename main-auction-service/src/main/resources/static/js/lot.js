const url = 'http://localhost:8080/api';
const urlParams = new URLSearchParams(window.location.search);
const lotId = urlParams.get('id');

function load() {
    fetch(url + '/lot/' + lotId)
        .then(response => response.json())
        .then(data => {
            // update title
            const title = document.querySelector('.header__title');
            title.innerText = data.name;

            // load lot info
            const currentGalleryImage =  document.querySelector('.description__image');
            currentGalleryImage.src = data.images[0];

            const descriptionTitle = document.querySelector('.description__title');
            descriptionTitle.innerHTML = data.name;

            const descriptionText = document.querySelector('.description__text');
            descriptionText.innerHTML = data.description;

            fetch(url + '/lot/' + lotId + '/top')
                    .then(response => response.json())
                    .then(data => {
                        const currentPrice = document.querySelector('.purchase__price-value');
                        currentPrice.innerHTML = getCurrency(data) + data.integerPart + "." + data.decimalPart;
                    })
                    .catch(error => console.error('Error while fetching data about lot:', error));

            cardsInfo[lotId] = {
                currentIndex: 0,
                imageUrls: data.images
            };
        })
        .catch(error => console.error('Error while fetching data about lot:', error));
}

function updatePrice() {
    fetch(url + '/lot/' + lotId + '/top')
        .then(response => response.json())
        .then(data => {
            const currentPrice = document.querySelector('.purchase__price-value');
            currentPrice.innerHTML = getCurrency(data) + data.integerPart + "." + data.decimalPart;
        })
        .catch(error => console.error('Error while fetching data about lot:', error));
}

function enableControls() {
    const leftControl = document.querySelector('.description__control_position_left');
    const rightControl = document.querySelector('.description__control_position_right');
    leftControl.addEventListener('click', () => {
        updatedImage(lotId, -1);
    });
    rightControl.addEventListener('click', () => {
        updatedImage(lotId, +1);
    });
}

document.addEventListener('DOMContentLoaded', function() {
    load();
    enableControls();
});