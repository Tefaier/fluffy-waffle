function getUserId() {
    return fetch('/api/user/userid', { method: 'GET' })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            return data;
        })
        .catch(error => {
            console.error('Error while fetching data about lots:', error);
            return null;
        });
}

// Usage example:
getUserId().then(userId => {
    console.log(userId);
});