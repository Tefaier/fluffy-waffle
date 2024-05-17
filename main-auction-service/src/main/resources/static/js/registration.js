const url = 'http://localhost:8080/api';

async function registration() {

    event.preventDefault();

    let inputFirstName;
    let inputLastName;
    let inputLogin;
    let inputPassword;
    let inputEmail;
    let inputReturnPassword;

    try {
        inputFirstName = document.querySelector("#first-name").value;
        inputLastName = document.querySelector("#last-name").value;
        inputLogin = document.querySelector("#username").value;
        inputEmail = document.querySelector("#email").value;
        inputPassword = document.querySelector("#password").value;
        inputReturnPassword = document.querySelector("#password-confirm").value;
        if (inputPassword !== inputReturnPassword) {
                    alert("Пароли не совпадают");
                    return;
                }
    } catch (error) {
        console.error(error);
        return;
    }

    const body = JSON.stringify({
        'login': inputLogin,
        'firstName': inputFirstName,
        'lastName' : inputLastName,
        'password' : inputPassword,
        'email': inputEmail,
        });

        const response = await fetch(url + "/user", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=UTF-8'
            },
            body: body,
        });
        const parsedUUID = await response.json();

        alert(`${inputFirstName}, вы успешно зарегистрировались`);


    // Добавить проверку на совпадения пароля
}
