const url = 'http://localhost:8081/api';


async function createLot() {
    //Объявляем переменные
    let inputName;
    let inputDescription;
    let inputURL;
    let inputInitialPrice;
    let inputIncrease;
    let inputStartDate;
    let inputFinishDate;
    let selectedCurrency;
    let initialPriceInteger;
    let initialPriceDecimal;
    let initialIncreasePriceInteger;
    let initialIncreasePriceDecimal;


    try {

        //получаем цену
        inputInitialPrice = document.querySelector("#initialPrice").value;

        let [integerPart, decimt] = inputInitialPrice.split('.');
                                       initialPriceInteger = parseIalParnt(integerPart);
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
        "userId": 65cb9107-2f43-4f4f-b878-d6d1d15b181c,
        "value": {
            "integerPart": initialPriceInteger,
            "decimalPart": initialPriceDecimal,
            "currency": selectedCurrency
        }
    });

    console.log(body);

    const response = await fetch("http://localhost:8081/api/user", {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                },
                body: body,
            });

    // console.log(body);
}