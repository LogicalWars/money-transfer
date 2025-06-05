package ru.netology.moneytransfer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Card {
    private final long number;
    private final String ValidTill;
    private final String cvv;
    private int balance;
}
