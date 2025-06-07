package ru.netology.moneytransfer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.moneytransfer.dto.request.ConfirmTransferRequest;
import ru.netology.moneytransfer.dto.response.TransferResponse;
import ru.netology.moneytransfer.exception.ApiException;
import ru.netology.moneytransfer.model.Card;
import ru.netology.moneytransfer.model.Transaction;
import ru.netology.moneytransfer.model.TransactionStatus;
import ru.netology.moneytransfer.repository.CardRepository;
import ru.netology.moneytransfer.repository.TransferRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmService {

    private final TransferRepository transferRepository;
    private final CardRepository cardRepository;

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
        value = (int) (value + Math.round(value * 0.01));

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
