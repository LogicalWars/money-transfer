package ru.netology.moneytransfer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.moneytransfer.dto.request.ConfirmTransferRequest;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConfirmServiceTest {
    @Mock
    private TransferRepository transferRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private ConfirmService confirmService;

    private ConfirmTransferRequest request;
    private Card cardFrom;
    private Card cardTo;
    private Transaction savedTransaction;

    @BeforeEach
    void setUp() {
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

        request = new ConfirmTransferRequest();
        request.setCode("0000");
        request.setOperationId(savedTransaction.getId().toString());
    }

    @Test
    void confirmTransfer_shouldChangeStatusTransactionAndChangeBalanceCard() {
        //arrange
        when(transferRepository.findById(savedTransaction.getId())).thenReturn(Optional.of(savedTransaction));

        //act
        TransferResponse response = confirmService.confirmTransfer(request);

        //asserts
        assertNotNull(response);
        assertEquals(savedTransaction.getId().toString(), response.getOperationId());

        assertEquals(TransactionStatus.SUCCESS, savedTransaction.getStatus());
        assertEquals(220_000, cardTo.getBalance());
        assertEquals(200_000, cardFrom.getBalance());
    }

    @Test
    void confirmTransfer_shouldThrowException_whenIncorrectVerificationCode() {
        //arrange
        request.setCode("1111");

        //act & asserts
        ApiException exception = assertThrows(ApiException.class, () -> {
            confirmService.confirmTransfer(request);
        });

        assertEquals("Verification code incorrect", exception.getMessage());
        assertEquals(4005, exception.getId());
    }

    @Test
    void confirmTransfer_shouldThrowException_whenTransactionNotFound() {
        //act & asserts
        ApiException exception = assertThrows(ApiException.class, () -> {
            confirmService.confirmTransfer(request);
        });

        assertEquals("Transfer not found", exception.getMessage());
        assertEquals(4007, exception.getId());
    }

    @Test
    void confirmTransfer_shouldThrowException_whenNotEnoughFunds() {
        //arrange
        Transaction incorrectTransaction = new Transaction(
                UUID.randomUUID(),
                cardFrom,
                cardTo,
                500_000,
                "RUR",
                LocalDateTime.now(),
                TransactionStatus.WAITED
        );
        when(transferRepository.findById(savedTransaction.getId())).thenReturn(Optional.of(incorrectTransaction));

        //act & asserts
        ApiException exception = assertThrows(ApiException.class, () -> {
            confirmService.confirmTransfer(request);
        });

        assertEquals("Not enough funds", exception.getMessage());
        assertEquals(4010, exception.getId());
    }

}
