package ru.netology.moneytransfer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.moneytransfer.dto.request.TransferRequest;
import ru.netology.moneytransfer.dto.response.TransferResponse;
import ru.netology.moneytransfer.exception.ApiException;
import ru.netology.moneytransfer.model.Card;
import ru.netology.moneytransfer.model.Transaction;
import ru.netology.moneytransfer.model.TransactionStatus;
import ru.netology.moneytransfer.repository.CardRepository;
import ru.netology.moneytransfer.repository.TransferRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private TransferService transferService;

    private TransferRequest request;
    private Card cardFrom;
    private Card cardTo;
    private Transaction savedTransaction;

    @BeforeEach
    void setUp() {
        request = new TransferRequest();
        request.setCardFromNumber("1234123412341234");
        request.setCardFromCVV("032");
        request.setCardFromValidTill("01/27");
        TransferRequest.Amount amount = new TransferRequest.Amount();
        amount.setCurrency("RUR");
        amount.setValue(200_000);
        request.setAmount(amount);
        request.setCardToNumber("4321432143214321");

        cardFrom = new Card(
                1234123412341234L,
                "01/27",
                "032",
                400_000
        );

        cardTo = new Card(
                4321432143214321L,
                "01/28",
                "013",
                20_000
        );

        savedTransaction = new Transaction(
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
    void processTransfer_shouldCreateTransaction() {
        //arrange
        when(cardRepository.findByNumberTillCVV(
                Long.parseLong(request.getCardFromNumber()),
                request.getCardFromValidTill(),
                request.getCardFromCVV()
        )).thenReturn(Optional.of(cardFrom));

        when(cardRepository.findByNumber(Long.parseLong(request.getCardToNumber()))).thenReturn(Optional.of(cardTo));

        when(transferRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        //act
        TransferResponse response = transferService.processTransfer(request);

        //asserts
        assertNotNull(response);
        assertEquals(savedTransaction.getId().toString(), response.getOperationId());

        //verify
        verify(cardRepository).findByNumberTillCVV(
                Long.parseLong(request.getCardFromNumber()),
                request.getCardFromValidTill(),
                request.getCardFromCVV()
        );
        verify(cardRepository).findByNumber(Long.parseLong(request.getCardToNumber()));
        verify(transferRepository).save(any(Transaction.class));
    }

    @Test
    void processTransfer_shouldThrowCardFromNotFound() {
        //arrange
        when(cardRepository.findByNumberTillCVV(
                Long.parseLong(request.getCardFromNumber()),
                request.getCardFromValidTill(),
                request.getCardFromCVV()))
                .thenReturn(Optional.empty());

        //act & asserts
        ApiException exception = assertThrows(ApiException.class, () -> {
            transferService.processTransfer(request);
        });

        assertEquals("Card not found " + Long.parseLong(request.getCardFromNumber()), exception.getMessage());
    }

    @Test
    void processTransfer_shouldThrowCardToNotFound() {
        //arrange
        when(cardRepository.findByNumberTillCVV(
                Long.parseLong(request.getCardFromNumber()),
                request.getCardFromValidTill(),
                request.getCardFromCVV()
        )).thenReturn(Optional.of(cardFrom));
        when(cardRepository.findByNumber(Long.parseLong(request.getCardToNumber()))).thenReturn(Optional.empty());

        //act & asserts
        ApiException exception = assertThrows(ApiException.class, () -> {
            transferService.processTransfer(request);
        });

        assertEquals("Card not found " + Long.parseLong(request.getCardToNumber()), exception.getMessage());
    }
}
