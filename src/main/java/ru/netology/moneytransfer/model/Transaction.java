package ru.netology.moneytransfer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Transaction {
    private final UUID id;
    private final Card cardFrom;
    private final Card cardTo;
    private final int amount;
    private final String currency;
    private final LocalDateTime date;
    private TransactionStatus status;
}
