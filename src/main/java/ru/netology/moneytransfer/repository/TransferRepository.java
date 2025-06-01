package ru.netology.moneytransfer.repository;

import ru.netology.moneytransfer.model.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface TransferRepository {
    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID id);
}
