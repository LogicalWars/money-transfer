package ru.netology.moneytransfer.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.moneytransfer.model.Card;
import ru.netology.moneytransfer.model.Transaction;
import ru.netology.moneytransfer.model.TransactionStatus;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TransferRepositoryInMemoryImplTest {

    private TransferRepositoryInMemoryImpl repository;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        repository = new TransferRepositoryInMemoryImpl();
        Card cardFrom = new Card(
                1234123412341234L,
                "01/27",
                "032",
                400_000
        );

        Card cardTo = new Card(
                4321432143214321L,
                "01/28",
                "013",
                20_000
        );

        transaction = new Transaction(
                UUID.randomUUID(),
                cardFrom,
                cardTo,
                200_000,
                "RUR",
                LocalDateTime.now(),
                TransactionStatus.WAITED
        );
    }

    @Test
    void save_shouldSave() {
        //act
        Transaction savedTransaction = repository.save(transaction);

        //asserts
        assertEquals(transaction, savedTransaction);
    }

    @Test
    void findById_shouldFind() {
        //arrange
        repository.save(transaction);

        //act
        Optional<Transaction> savedTransaction = repository.findById(transaction.getId());

        //asserts
        assertEquals(Optional.of(transaction), savedTransaction);
    }

    @Test
    void findById_shouldReturnEmpty() {
        //act
        Optional<Transaction> savedTransaction = repository.findById(transaction.getId());

        //asserts
        assertEquals(Optional.empty(), savedTransaction);
    }
}