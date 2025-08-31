package co.com.crediya.requests.model.typeloan;

import lombok.Getter;

@Getter
public enum TypeLoanEnum {
    PERSONAL("PERSONAL"), // For personal purposes, such as travel, emergencies or shopping.
    CONSUMER("CONSUMER"), // Intended for the purchase of specific goods and services, such as household appliances or furniture.
    MORTGAGE("MORTGAGE"), // Aimed at the acquisition of a home or property.
    AUTOMOTIVE("AUTOMOTIVE"); // Specifically for the purchase of a vehicle.

    private final String value;

    TypeLoanEnum(String value) {
        this.value = value;
    }
}
