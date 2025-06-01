package ru.netology.moneytransfer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Подтверждение перевода денег")
public class ConfirmTransferRequest {

    @Schema(description = "Id операции")
    @NotBlank(message = "Id операции не может быть пустым")
    private String operationId;

    @Schema(description = "Код верификации")
    @NotBlank(message = "Код верификации не может быть пустым")
    private String code;
}
