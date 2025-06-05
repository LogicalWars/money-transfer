package ru.netology.moneytransfer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.moneytransfer.dto.request.TransferRequest;
import ru.netology.moneytransfer.dto.response.TransferResponse;
import ru.netology.moneytransfer.model.Transaction;
import ru.netology.moneytransfer.model.TransactionStatus;
import ru.netology.moneytransfer.repository.TransferRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransferRepository repository;

    public TransferResponse processTransfer(TransferRequest request) {
        Transaction transaction = new Transaction(
                UUID.randomUUID(),
                Long.parseLong(request.getCardFromNumber()),
                request.getCardFromValidTill(),
                Integer.parseInt(request.getCardFromCVV()),
                Long.parseLong(request.getCardToNumber()),
                request.getAmount().getValue(),
                request.getAmount().getCurrency(),
                LocalDateTime.now(),
                TransactionStatus.WAITED
        );
        log.info("This is transaction: [{}]", transaction);

        return new TransferResponse(repository.save(transaction).getId().toString());
    }
}
