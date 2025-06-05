package ru.netology.moneytransfer.repository;

import org.springframework.stereotype.Repository;
import ru.netology.moneytransfer.model.Card;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CardRepositoryInMemoryImpl implements CardRepository {

    private final ConcurrentHashMap<Long, Card> listOfCard = initCards();

    @Override
    public Card save(Card card) {
        listOfCard.putIfAbsent(card.getNumber(), card);
        return card;
    }

    @Override
    public Optional<Card> findByNumberTillCVV(long number, String till, String cvv) {
        return findByNumber(number).stream()
                .filter(c -> till.equals(c.getValidTill()))
                .filter(c -> cvv.equals(c.getCvv()))
                .findFirst();
    }


    @Override
    public Optional<Card> findByNumber(long number) {
        return Optional.ofNullable(listOfCard.get(number));
    }

    private static ConcurrentHashMap<Long, Card> initCards() {
        ConcurrentHashMap<Long, Card> map = new ConcurrentHashMap<>();
        long number1 = Long.parseLong("1234123412341234");
        long number2 = Long.parseLong("4321432143214321");
        map.put(number1, new Card(number1, "01/27", "032", 10_000));
        map.put(number2, new Card(number2, "04/32", "103", 100_000));
        return map;
    }
}
