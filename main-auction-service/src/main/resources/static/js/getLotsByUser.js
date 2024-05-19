async function getLotsByUser() {
     event.preventDefault();
    let userId = await getUserId();

    fetch('http://localhost:8080/api' + '/user' + userId + '/lots', {method: 'GET'}
    ).then(response => response.json())
    .then(data => console.log(data));
}