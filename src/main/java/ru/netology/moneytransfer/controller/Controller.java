package ru.netology.moneytransfer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.moneytransfer.dto.request.ConfirmTransferRequest;
import ru.netology.moneytransfer.dto.request.TransferRequest;
import ru.netology.moneytransfer.dto.response.TransferResponse;
import ru.netology.moneytransfer.service.ConfirmService;
import ru.netology.moneytransfer.service.TransferService;

@RestController
@Tag(name = "Переводы", description = "API для денежных переводов")
@RequiredArgsConstructor
public class Controller {
    private final TransferService transferService;
    private final ConfirmService confirmTransfer;

    @PostMapping("/transfer")
    @Operation(summary = "Создать перевод", description = "Перевод денег с карты на карту")
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(transferService.processTransfer(request));
    }

    @PostMapping("/confirmOperation")
    @Operation(summary = "Подтверждение перевода", description = "Подтвердить ранее оформленный перевод")
    public ResponseEntity<?> confirmTransfer(@Valid @RequestBody ConfirmTransferRequest request) {
        return ResponseEntity.ok(confirmTransfer.confirmTransfer(request));
    }
}
