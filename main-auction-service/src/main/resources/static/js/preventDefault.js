document.addEventListener('click', function(event) {
    const controlElement = event.target.closest('.card__control');
    if (controlElement) {
        event.preventDefault();
    }
});
