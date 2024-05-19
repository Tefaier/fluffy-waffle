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

            if (isNaN(PriceDecimal)) {
                        PriceDecimal = 0;
                    }
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

        var currentUrl = window.location.href;
        var lotId = await getLotIdFromUrl(currentUrl);

        let userId = await getUserId();
        //Собираем json
        const body = JSON.stringify({
                "userId": userId,
                "lotId": lotId,
                "value": {
                    "integerPart": PriceInteger,
                    "decimalPart": PriceDecimal,
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

function getLotIdFromUrl(url) {
    // Получаем параметры URL-адреса
    var params = new URLSearchParams(url.split('?')[1]);
    // Получаем значение параметра 'id'
    var lotId = params.get('id');
    console.log(lotId)
    return lotId;
}
