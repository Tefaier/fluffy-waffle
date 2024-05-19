const cardsInfo = {};

function updatedImage(id, shift) {
    const card = document.getElementById(id);
    if (card) {
        const image = card.querySelector('.card__image');
        let currentIndex = cardsInfo[id].currentIndex;
        const imageUrls = cardsInfo[id].imageUrls;
        if (-1 < currentIndex + shift && currentIndex + shift < imageUrls.length) {
            currentIndex += shift;
        }
        image.src = imageUrls[currentIndex];
        cardsInfo[id].currentIndex = currentIndex;
        return;
    }
    const gallery = document.querySelector('.description__gallery');
    if (gallery) {
        const image = gallery.querySelector('.description__image');
        let currentIndex = cardsInfo[id].currentIndex;
        const imageUrls = cardsInfo[id].imageUrls;
        if (-1 < currentIndex + shift && currentIndex + shift < imageUrls.length) {
            currentIndex += shift;
        }
        image.src = imageUrls[currentIndex];
        cardsInfo[id].currentIndex = currentIndex;
    }
}
