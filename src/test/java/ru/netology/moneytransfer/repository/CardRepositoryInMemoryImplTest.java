package ru.netology.moneytransfer.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.moneytransfer.model.Card;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CardRepositoryInMemoryImplTest {

    private CardRepositoryInMemoryImpl repository;
    private Card card;

    @BeforeEach
    void setUp() {
        repository = new CardRepositoryInMemoryImpl();
        card = new Card(
                3456345634563456L,
                "04/27",
                "435",
                23_000
        );
    }

    @Test
    void save() {
        Card savedCard = repository.save(card);
        assertEquals(card, savedCard);
    }

    @Test
    void findByNumberTillCVV() {
        repository.save(card);
        Optional<Card> foundCard = repository.findByNumberTillCVV(card.getNumber(), card.getValidTill(), card.getCvv());
        assertEquals(Optional.of(card), foundCard);
    }

    @Test
    void findByNumberTillCVV_shouldReturnEmpty() {
        Optional<Card> foundCard = repository.findByNumberTillCVV(card.getNumber(), card.getValidTill(), card.getCvv());
        assertEquals(Optional.empty(), foundCard);
    }

    @Test
    void findByNumber() {
        repository.save(card);
        Optional<Card> foundCard = repository.findByNumber(card.getNumber());
        assertEquals(Optional.of(card), foundCard);
    }

    @Test
    void findByNumber_shouldReturnEmpty() {
        Optional<Card> foundCard = repository.findByNumber(card.getNumber());
        assertEquals(Optional.empty(), foundCard);
    }
}