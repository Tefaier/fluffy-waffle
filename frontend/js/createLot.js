const url = 'http://localhost:5432/api';

function createLot() {




    const body = JSON.stringify({
        "userId": "<uuid>",
        "initialPrice": {
            "integerPart": "<long>",
            "decimalPart": "<long>",
            "currency": "USD"
        },
        "minimumIncrease": {
            "integerPart": "<long>",
            "decimalPart": "<long>",
            "currency": "USD"
        },
        "startTime": "<dateTime>",
        "finishTime": "<dateTime>",
        "description": "<string>",
        "images": [
            "<string>",
            "<string>"
        ]
    });
}