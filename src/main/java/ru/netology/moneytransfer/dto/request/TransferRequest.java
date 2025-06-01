package ru.netology.moneytransfer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Запрос на перевод денег")
public class TransferRequest {

    @Schema(description = "Номер карты отправителя", example = "1234123412341234")
    @NotBlank(message = "Номер карты отправителя обязателен")
    @Pattern(regexp = "\\d{16}", message = "Номер карты должен содержать 16 цифр")
    private String cardFromNumber;

    @Schema(description = "Срок действия карты отправителя", example = "01/27")
    @NotBlank(message = "Срок действия карты обязателен")
    @Pattern(regexp = "(0[1-9]|1[0-2])/[0-9]{2}", message = "Формат должен быть MM/YY")
    private String cardFromValidTill;

    @Schema(description = "CVV код карты отправителя", example = "345")
    @NotBlank(message = "CVV код обязателен")
    @Pattern(regexp = "\\d{3,4}", message = "CVV должен содержать 3 или 4 цифры")
    private String cardFromCVV;

    @Schema(description = "Номер карты получателя", example = "2345234523452345")
    @NotBlank(message = "Номер карты получателя обязателен")
    @Pattern(regexp = "\\d{16}", message = "Номер карты должен содержать 16 цифр")
    private String cardToNumber;

    @Data
    @Schema(description = "Сумма перевода")
    public static class Amount {

        @Schema(description = "Сумма перевода", example = "245")
        @NotNull(message = "Сумма не может быть null")
        @Min(value = 1, message = "Сумма не может быть меньше 1")
        private Integer value;

        @Schema(description = "Валюта", example = "RUR")
        @NotBlank(message = "Валюта не может быть пустой")
        @Pattern(regexp = "RUR", message = "Валюта может быть только RUR")
        private String currency;
    }


    @Valid
    @Schema(description = "Сумма перевода")
    private Amount amount;
}
