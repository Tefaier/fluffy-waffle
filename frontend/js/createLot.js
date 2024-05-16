const url = 'http://localhost:8080/api';

async function createLot() {

//    event.preventDefault();

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
        //имя
        inputName = document.querySelector("#name").value;
        //описание
        inputDescription = document.querySelector("#description").value;
        //images
        inputURL = document.querySelector("#images").value;

        //получаем изначальную цену
        inputInitialPrice = document.querySelector("#initialPrice").value;

        let [integerPart, decimalPart] = inputInitialPrice.split('.');
        initialPriceInteger = parseInt(integerPart);
        initialPriceDecimal = parseInt(decimalPart);

        //получаем минимальную для увеличения
        inputIncrease = document.querySelector("#increasePrice").value;

        let [integerIncreasePart, decimalIncreasePart] = inputIncrease.split('.');
        initialIncreasePriceInteger = parseInt(integerIncreasePart);
        initialIncreasePriceDecimal = parseInt(decimalIncreasePart);

        //получаем начало и конец
        inputStartDate = document.querySelector("#start-date").value;
        inputFinishDate = document.querySelector("#end-date").value;

        //получаем валюту
        selectedCurrency = document.querySelector('input[name="currency"]:checked').value;


    } catch (error) {
        console.error(error);
        return;
    }

    //Собираем json
    const body = JSON.stringify({
        "userId": "f554ee9f-1c6d-4d83-a87d-d45f2100e12d",
        "initialPrice": {
            "integerPart": initialPriceInteger,
            "decimalPart": initialPriceDecimal,
            "currency": selectedCurrency
        },
        "minimumIncrease": {
            "integerPart": initialIncreasePriceInteger,
            "decimalPart": initialIncreasePriceDecimal,
            "currency": selectedCurrency
        },
        "startTime": inputStartDate,
        "finishTime": inputFinishDate,
        "description": inputDescription,
        "images": [
            inputURL,
            inputURL
        ]
    });

    const response = await fetch(url + "/lot", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                },
                body: body,
            });

    console.log(body);
}