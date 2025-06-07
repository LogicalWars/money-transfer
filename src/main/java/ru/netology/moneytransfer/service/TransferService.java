package ru.netology.moneytransfer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.moneytransfer.dto.request.ConfirmTransferRequest;
import ru.netology.moneytransfer.dto.request.TransferRequest;
import ru.netology.moneytransfer.dto.response.TransferResponse;
import ru.netology.moneytransfer.exception.ApiException;
import ru.netology.moneytransfer.model.Card;
import ru.netology.moneytransfer.model.Transaction;
import ru.netology.moneytransfer.model.TransactionStatus;
import ru.netology.moneytransfer.repository.CardRepository;
import ru.netology.moneytransfer.repository.TransferRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransferRepository transferRepository;
    private final CardRepository cardRepository;

    public TransferResponse processTransfer(TransferRequest request) {
        Card cardFrom = cardRepository.findByNumberTillCVV(
                Long.parseLong(request.getCardFromNumber()),
                request.getCardFromValidTill(),
                request.getCardFromCVV()
        ).orElseThrow(() -> new ApiException("Card not found " + Long.parseLong(request.getCardFromNumber()), 4002));

        Card cardTo = cardRepository.findByNumber(
                Long.parseLong(request.getCardToNumber())
        ).orElseThrow(() -> new ApiException("Card not found " + Long.parseLong(request.getCardToNumber()), 4002));

        UUID idTransaction = UUID.randomUUID();

        int amountValue = 0;
        if (request.getAmount().getCurrency().equals("RUR")) {
            amountValue = request.getAmount().getValue() / 100;
        }
        Transaction transaction = new Transaction(
                idTransaction,
                cardFrom,
                cardTo,
                amountValue,
                request.getAmount().getCurrency(),
                LocalDateTime.now(),
                TransactionStatus.WAITED
        );
        log.info("Transaction: {}, amount {}, from {}, to {}", idTransaction, transaction.getAmount(), cardFrom, cardTo);

        return new TransferResponse(transferRepository.save(transaction).getId().toString());
    }
}
