package ru.netology.moneytransfer.dto.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TransferResponse {
    private final String operationId;
}
