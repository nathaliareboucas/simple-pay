package br.com.reboucas.nathalia.simple_pay.integration_tests.transfer.controllers.v1;

import br.com.reboucas.nathalia.simple_pay.integration_tests.config.ContainersConfig;
import br.com.reboucas.nathalia.simple_pay.notification.services.TransactionDTO;
import br.com.reboucas.nathalia.simple_pay.transfer.controllers.v1.dtos.TransferDTO;
import br.com.reboucas.nathalia.simple_pay.wallet.Wallet;
import br.com.reboucas.nathalia.simple_pay.wallet.repositories.WalletRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ProblemDetail;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static br.com.reboucas.nathalia.simple_pay.wallet.WalletType.COMMON;
import static br.com.reboucas.nathalia.simple_pay.wallet.WalletType.SHOPKEEPER;
import static io.restassured.RestAssured.given;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Testcontainers
@Import(ContainersConfig.class)
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TransferControllerTest {
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final int PORT = 8888;

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    @Autowired
    WalletRepository walletRepository;

    @BeforeAll
    static void setup() {
        objectMapper = new ObjectMapper();
        specification = new RequestSpecBuilder()
                .setBasePath("/v1/api/transfer")
                .setPort(PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    void shouldTransferSuccessfullyWhenAuthorized() throws JsonProcessingException {
        var commonWallet = walletRepository.save(buildCommonWallet());
        var shopkeeperWallet = walletRepository.save(buildShopkeeperWallet());
        var transferDTO = new TransactionDTO(commonWallet.getId(), shopkeeperWallet.getId(), TEN);

        Response response = given().spec(specification).contentType(CONTENT_TYPE_JSON)
                .when().body(transferDTO).post()
                .then().extract().response();

        if (response.statusCode() == 201) {
            var transferDTOResponse = objectMapper.readValue(response.body().asString(), TransferDTO.class);
            assertNotNull(transferDTOResponse);
            assertEquals(transferDTO.value(), transferDTOResponse.value());
            assertEquals(transferDTO.payer(), transferDTOResponse.payer());
            assertEquals(transferDTO.payee(), transferDTOResponse.payee());
        } else {
            var problemDetailResponse = objectMapper.readValue(response.body().asString(), ProblemDetail.class);
            assertNotNull(problemDetailResponse);
            assertEquals("SimplePay Exception", problemDetailResponse.getTitle());
            assertEquals("Transação não autorizada", problemDetailResponse.getDetail());
            assertEquals(UNPROCESSABLE_ENTITY.value(), problemDetailResponse.getStatus());
        }
    }

    @ParameterizedTest
    @MethodSource()
    @Order(2)
    void shouldNotTransferWhenValidationError(TransferDTO transferDTO, int statusCode, String errorMessage)
            throws JsonProcessingException {
        String responseJson = given().spec(specification).contentType(CONTENT_TYPE_JSON)
                .when().body(transferDTO).post()
                .then().statusCode(statusCode).extract().body().asString();

        var problemDetailResponse = objectMapper.readValue(responseJson, ProblemDetail.class);

        assertNotNull(problemDetailResponse);
        assertEquals("SimplePay Exception", problemDetailResponse.getTitle());
        assertEquals(errorMessage, problemDetailResponse.getDetail());
        assertEquals(statusCode, problemDetailResponse.getStatus());
    }

    static Stream<Arguments> shouldNotTransferWhenValidationError() {
        return Stream.of(
                Arguments.of(new TransferDTO(TEN, 3L, 2L),
                        NOT_FOUND.value(), "Carteira 3 não encontrada"),
                Arguments.of(new TransferDTO(TEN, 1L, 4L),
                        NOT_FOUND.value(), "Carteira 4 não encontrada"),
                Arguments.of(new TransferDTO(ZERO, 1L, 2L),
                        UNPROCESSABLE_ENTITY.value(), "Valor inválido"),
                Arguments.of(new TransferDTO(new BigDecimal(-1), 1L, 2L),
                        UNPROCESSABLE_ENTITY.value(), "Valor inválido"),
                Arguments.of(new TransferDTO(TEN, 1L, 1L),
                        UNPROCESSABLE_ENTITY.value(), "Beneficiário inválido"),
                Arguments.of(new TransferDTO(TEN, 2L, 1L),
                        UNPROCESSABLE_ENTITY.value(), "Carteira do tipo lojista não pode realizar transferências"),
                Arguments.of(new TransferDTO(new BigDecimal(20), 1L, 2L),
                        UNPROCESSABLE_ENTITY.value(), "Carteira 1 com saldo insuficiente"));
    }

    private static Wallet buildCommonWallet() {
        return Wallet.builder()
                .fullName("Full Name Payer")
                .cpfCnpj("12345678900")
                .email("emailpayer@teste.com")
                .password("12345678")
                .balance(TEN)
                .walletType(COMMON)
                .build();
    }

    private static Wallet buildShopkeeperWallet() {
        return Wallet.builder()
                .fullName("Full Name Payee")
                .cpfCnpj("12345678912345")
                .email("emailpayee@teste.com")
                .password("12345678")
                .balance(TEN)
                .walletType(SHOPKEEPER)
                .build();
    }
}
