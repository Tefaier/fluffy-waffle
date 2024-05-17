const urlMakeBet = "/api";

async function makeBet() {

    let inputPrice;

    let priceInteger;
    let priceDecimal;
    let selectedCurrency;


    try {
            //получаем цену
            inputPrice = document.querySelector("#bet-value").value;

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
        let userId = await getUserId();
        //Собираем json
        const body = JSON.stringify({
                "userId": "65cb9107-2f43-4f4f-b878-d6d1d15b181c",
                "lotId": 1,
                "value": {
                    "integerPart": 1000,
                    "decimalPart": 0,
                    "currency": selectedCurrency
                }
            });

        const response = await fetch(urlMakeBet + "/bet/make", {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json;charset=UTF-8'
                        },
                        body: body,
                    });

        console.log(body);
}
