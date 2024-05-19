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
    let linksArray;
    let textarea;

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
        if (isNaN(initialPriceDecimal)) {
            initialPriceDecimal = 0;
        }

        //получаем минимальную для увеличения
        inputIncrease = document.querySelector("#increasePrice").value;

        let [integerIncreasePart, decimalIncreasePart] = inputIncrease.split('.');
        initialIncreasePriceInteger = parseInt(integerIncreasePart);
        initialIncreasePriceDecimal = parseInt(decimalIncreasePart);
        if (isNaN(initialIncreasePriceDecimal)) {
            initialIncreasePriceDecimal = 0;
        }

        //получаем начало и конец
        inputStartDate = document.querySelector("#start-date").value;
        inputFinishDate = document.querySelector("#end-date").value;

        //получаем валюту
        selectedCurrency = document.querySelector('input[name="currency"]:checked').value;

        textarea = document.getElementById('images');
        linksArray = textarea.value
          .trim() // Удалить лишние пробелы
          .split('\n') // Разбить по строкам
          .filter(link => link !== '');

        console.log(linksArray);

    } catch (error) {
        console.error(error);
        return;
    }

    //Собираем json
    let userId = await getUserId();
    const body = JSON.stringify({
        "userId": userId,
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
        "name" : inputName,
        "description": inputDescription,
        "images": linksArray
    });

    console.log(body);

    const response = await fetch(url + "/lot", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                },
                body: body,
            });
    alert("You've created a new lot");
    // console.log(body);
}