package ru.netology.moneytransfer.repository;

import org.springframework.stereotype.Repository;
import ru.netology.moneytransfer.model.Transaction;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TransferRepositoryInMemoryImpl implements TransferRepository {

    private final ConcurrentHashMap<UUID, Transaction> listOfTransaction = new ConcurrentHashMap<>();

    @Override
    public Transaction save(Transaction transaction) {
        listOfTransaction.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return Optional.ofNullable(listOfTransaction.get(id));
    }
}
