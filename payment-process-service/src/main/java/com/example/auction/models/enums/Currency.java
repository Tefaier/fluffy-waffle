package com.example.auction.models.enums;

public enum Currency {
    RUB(1),
    USD(92.58f),
    EUR(100.44f);

    private final float comparativeValue;

    Currency(float comparativeValue) {
        this.comparativeValue = comparativeValue;
    }

    public float getComparativeValue() {
        return comparativeValue;
    }
}
