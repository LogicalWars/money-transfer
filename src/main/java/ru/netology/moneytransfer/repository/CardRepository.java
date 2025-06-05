package ru.netology.moneytransfer.repository;

import ru.netology.moneytransfer.model.Card;

import java.util.Optional;

public interface CardRepository {
    Card save(Card card);

    Optional<Card> findByNumberTillCVV (long number, String till, String cvv);

    Optional<Card> findByNumber (long number);
}
