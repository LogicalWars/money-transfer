package ru.netology.moneytransfer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class Transaction {
    private final UUID id;
    private final long cardFromNumber;
    private final String cardFromValidTill;
    private final int cardFromCVV;
    private final long cardToNumber;
    private final int amount;
    private final String currency;
    private final LocalDateTime date;
    private final TransactionStatus status;
}
