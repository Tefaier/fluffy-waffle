function getCurrency(money) {
    switch (money.currency) {
        case "USD":
            return "$";
        case "RUB":
            return "₽";
        case "EUR":
            return "€";
        default:
            return "";
    }
}

function getCurrentPrice(bets, initialPrice) {
    if (bets.length === 0) {
        return moneyValue(initialPrice);
    }
    return betValue(bets.reduce((max, currentBet) => {
        return betValue(currentBet) > betValue(max) ? currentBet : max;
    }, bets[0]));
}

function betValue(bet) {
    return moneyValue(bet.value);
}

function moneyValue(money) {
    return money.integerPart + money.decimalPart / Math.pow(10, money.decimalPart.toString().length);
}