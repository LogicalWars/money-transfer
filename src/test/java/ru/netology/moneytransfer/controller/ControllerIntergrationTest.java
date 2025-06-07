package ru.netology.moneytransfer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.netology.moneytransfer.dto.error.ErrorResponse;
import ru.netology.moneytransfer.dto.request.ConfirmTransferRequest;
import ru.netology.moneytransfer.dto.request.TransferRequest;
import ru.netology.moneytransfer.dto.response.TransferResponse;
import ru.netology.moneytransfer.repository.CardRepositoryInMemoryImpl;
import ru.netology.moneytransfer.repository.TransferRepositoryInMemoryImpl;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private TransferRequest transferRequest;
    private ConfirmTransferRequest confirmTransferRequest;
    private String transferId;

    @Autowired
    private TransferRepositoryInMemoryImpl transferRepositoryInMemoryImpl;
    @Autowired
    private CardRepositoryInMemoryImpl cardRepositoryInMemoryImpl;

    @BeforeEach
    void setUp() {
        transferRequest = new TransferRequest();
        transferRequest.setCardFromNumber("1234123412341234");
        transferRequest.setCardFromCVV("032");
        transferRequest.setCardFromValidTill("01/27");
        TransferRequest.Amount amount = new TransferRequest.Amount();
        amount.setCurrency("RUR");
        amount.setValue(200_000);
        transferRequest.setAmount(amount);
        transferRequest.setCardToNumber("4321432143214321");

        ResponseEntity<TransferResponse> response = restTemplate.postForEntity("/transfer", transferRequest, TransferResponse.class);
        transferId = response.getBody().getOperationId();
        confirmTransferRequest = new ConfirmTransferRequest();
        confirmTransferRequest.setCode("0000");
        confirmTransferRequest.setOperationId(transferId);
    }

    @Test
    void transfer_shouldReturnOk() {
        ResponseEntity<TransferResponse> response = restTemplate.postForEntity("/transfer", transferRequest, TransferResponse.class);
        String savedId = String.valueOf(
                transferRepositoryInMemoryImpl.findById(
                                UUID.fromString(response.getBody().getOperationId()))
                        .get()
                        .getId()
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedId, response.getBody().getOperationId());
    }

    @Test
    void confirmTransfer_shouldReturnOk() {
        ResponseEntity<TransferResponse> response = restTemplate.postForEntity("/confirmOperation", confirmTransferRequest, TransferResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(transferId, response.getBody().getOperationId());
    }

    @Test
    void transfer_shouldReturnBadRequestValidFailed() {
        transferRequest.setCardFromNumber("341b");
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/transfer", transferRequest, ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Номер карты должен содержать 16 цифр", response.getBody().getMessage());
    }

    @Test
    void transfer_shouldReturnBadRequestNotFound() {
        String cardNumber = "5432543254325432";
        transferRequest.setCardFromNumber(cardNumber);
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/transfer", transferRequest, ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Card not found " + cardNumber, response.getBody().getMessage());
    }

    @Test
    void confirmTransfer_shouldReturnServerError() {
        confirmTransferRequest.setOperationId("gergihsdgierwgpaj");
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/confirmOperation", confirmTransferRequest, ErrorResponse.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void confirmTransfer_shouldReturnBadRequestVerificationFailed() {
        confirmTransferRequest.setCode("1234");
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/confirmOperation", confirmTransferRequest, ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Verification code incorrect", response.getBody().getMessage());
    }

    @Test
    void confirmTransfer_shouldReturnBadRequestNotEnoughFunds() {
        cardRepositoryInMemoryImpl.findByNumber(1234123412341234L).get().setBalance(10);
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/confirmOperation", confirmTransferRequest, ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not enough funds", response.getBody().getMessage());
    }
}