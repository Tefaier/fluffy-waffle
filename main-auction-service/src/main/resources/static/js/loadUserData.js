async function loadUserData() {
    const userId = await getUserId();
    fetch('http://localhost:8080/api/user/' + userId, {method: 'GET'})
        .then(response => response.json()).then(user => {
            console.log(user);
            const firstName = document.getElementById('first-name');
            const lastName = document.getElementById('last-name');
            const email = document.getElementById('email');
            const username = document.getElementById('username');
            const password = document.getElementById('password');

            firstName.value = user.firstName;
            lastName.value = user.lastName;
            email.value = user.email;
            username.value = user.login;
            password.value = '';
    });
}

document.addEventListener('DOMContentLoaded', function() {
    loadUserData();
});