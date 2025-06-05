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

    public TransferResponse confirmTransfer(ConfirmTransferRequest request) {
        if (!request.getCode().equals("0000")) {
            throw new ApiException("Verification code incorrect", 4005);
        }

        transferMoney(transferRepository.findById(UUID.fromString(request.getOperationId()))
                .orElseThrow(() -> new ApiException("Transfer not found", 4007))
        );


        return new TransferResponse(
                String.valueOf(this.changeStatus(UUID.fromString(request.getOperationId()), TransactionStatus.SUCCESS).getId()));
    }

    private Transaction changeStatus(UUID id, TransactionStatus status) {
        return transferRepository.findById(id)
                .map(t -> {
                    t.setStatus(status);
                    return t;
                })
                .orElseThrow(() -> new ApiException("Transfer not found", 4004));
    }

    private void transferMoney(Transaction transaction) {
        Card cardFrom = transaction.getCardFrom();
        Card cardTo = transaction.getCardTo();
        int value = transaction.getAmount();

        if (cardFrom.getBalance() < value) {
            transaction.setStatus(TransactionStatus.DECLINE);
            transferRepository.save(transaction);
            throw new ApiException("Not enough funds", 4010);
        }
        cardFrom.setBalance(cardFrom.getBalance() - value);
        cardTo.setBalance(cardTo.getBalance() + value);
        cardRepository.save(cardFrom);
        cardRepository.save(cardTo);

        log.info("Confirm transaction: {}, amount {}, from {}, to {}", transaction.getId(), transaction.getAmount(), cardFrom, cardTo);
    }
}
