const url = "/api";

async function makeBet() {

    let inputPrice;

    let priceInteger;
    let priceDecimal;
    let selectedCurrency;


    try {
            //получаем цену
            inputPrice = document.querySelector("#price").value;

            let [integerPart, decimalPart] = inputPrice.split('.');
            PriceInteger = parseInt(integerPart);
            PriceDecimal = parseInt(decimalPart);
//            //получаем минимальную для увеличения
//            inputIncrease = document.querySelector("#increasePrice").value;
//
//            let [integerIncreasePart, decimalIncreasePart] = inputIncrease.split('.');
//            initialIncreasePriceInteger = parseInt(integerIncreasePart);
//            initialIncreasePriceDecimal = parseInt(decimalIncreasePart);
//
//            //получаем начало и конец
//            inputStartDate = document.querySelector("#start-date").value;
//            inputFinishDate = document.querySelector("#end-date").value;

            //получаем валюту
            selectedCurrency = document.querySelector('input[name="currency"]:checked').value;

        } catch (error) {
            console.error(error);
            return;
        }

        //Собираем json
        const body = JSON.stringify({
                "userId": "8f6a6ef3-0d0c-4eea-8f83-65b083975f23",
                "lotId": 1,
                "value": {
                    "integerPart": priceInteger,
                    "decimalPart": priceDecimal,
                    "currency": selectedCurrency
                }
            });

        const response = await fetch(url + "/bet", {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json;charset=UTF-8'
                        },
                        body: body,
                    });

        console.log(body);
}
