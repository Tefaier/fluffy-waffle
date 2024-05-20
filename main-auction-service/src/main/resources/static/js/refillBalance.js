const url = 'http://localhost:8081/api';


async function refill() {
    //Объявляем переменные
    let inputInitialPrice;
    let selectedCurrency;
    let initialPriceInteger;
    let initialPriceDecimal;


    try {

        //получаем цену
        inputInitialPrice = document.querySelector("#initialPrice").value;

        let [integerPart, decimalPart] = inputInitialPrice.split('.');
        initialPriceInteger = parseInt(integerPart);
        initialPriceDecimal = parseInt(decimalPart);
        if (isNaN(initialPriceDecimal)) {
            initialPriceDecimal = 0;
        }


        //получаем валюту
        selectedCurrency = document.querySelector('input[name="currency"]:checked').value;


    } catch (error) {
        console.error(error);
        return;
    }

    //Собираем json
    let userId = await getUserId();
    const body = JSON.stringify({
        "userId": userId,
        "value": {
            "integerPart": initialPriceInteger,
            "decimalPart": initialPriceDecimal,
            "currency": selectedCurrency
        }
    });

    console.log(body);

    const response = await fetch("http://localhost:8081/api/user", {
//        mode: 'no-cors',
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json;charset=UTF-8',
          'Accept': 'application/json'
        },
        body: body,
    });
    alert("You've filled your balance");
}