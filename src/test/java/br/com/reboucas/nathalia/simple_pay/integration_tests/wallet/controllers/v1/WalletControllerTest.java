package br.com.reboucas.nathalia.simple_pay.integration_tests.wallet.controllers.v1;

import br.com.reboucas.nathalia.simple_pay.integration_tests.config.ContainersConfig;
import br.com.reboucas.nathalia.simple_pay.wallet.WalletType;
import br.com.reboucas.nathalia.simple_pay.wallet.controllers.v1.dtos.WalletDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.testcontainers.junit.jupiter.Testcontainers;

import static br.com.reboucas.nathalia.simple_pay.wallet.WalletType.COMMON;
import static br.com.reboucas.nathalia.simple_pay.wallet.WalletType.SHOPKEEPER;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@Import(ContainersConfig.class)
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class WalletControllerTest {
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final int PORT = 8888;

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setup() {
        objectMapper = new ObjectMapper();
        specification = new RequestSpecBuilder()
                .setBasePath("/v1/api/wallet")
                .setPort(PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    void shouldCreateCommonWallet() throws JsonProcessingException {
        var walletDTO = buildWalletDTO(COMMON);
        var responseJson = given().spec(specification).contentType(CONTENT_TYPE_JSON)
                .when().body(walletDTO).post()
                .then().statusCode(HttpStatus.CREATED.value()).extract().body().asString();

        var walletDTOResponse = objectMapper.readValue(responseJson, WalletDTO.class);

        assertNotNull(walletDTOResponse);
        assertEquals(walletDTO.fullName(), walletDTOResponse.fullName());
        assertEquals(walletDTO.cpfCnpj(), walletDTOResponse.cpfCnpj());
        assertEquals(walletDTO.email(), walletDTOResponse.email());
        assertEquals(walletDTO.password(), walletDTOResponse.password());
        assertEquals(walletDTO.balance(), walletDTOResponse.balance());
        assertEquals(walletDTO.walletType(), walletDTOResponse.walletType());
    }

    @Test
    @Order(2)
    void shouldCreateShopkeeperWallet() throws JsonProcessingException {
        var walletDTO = buildWalletDTO(SHOPKEEPER);
        String responseJson = given().spec(specification).contentType(CONTENT_TYPE_JSON)
                .when().body(walletDTO).post()
                .then().statusCode(HttpStatus.CREATED.value()).extract().body().asString();

        var walletDTOResponse = objectMapper.readValue(responseJson, WalletDTO.class);

        assertNotNull(walletDTOResponse);
        assertEquals(walletDTO.fullName(), walletDTOResponse.fullName());
        assertEquals(walletDTO.cpfCnpj(), walletDTOResponse.cpfCnpj());
        assertEquals(walletDTO.email(), walletDTOResponse.email());
        assertEquals(walletDTO.password(), walletDTOResponse.password());
        assertEquals(walletDTO.balance(), walletDTOResponse.balance());
        assertEquals(walletDTO.walletType(), walletDTOResponse.walletType());
    }

    @Test
    @Order(3)
    void shouldNotCreateWallet() throws JsonProcessingException {
        var walletDTO = buildWalletDTO(COMMON);
        String responseJson = given().spec(specification).contentType(CONTENT_TYPE_JSON)
                .when().body(walletDTO).post()
                .then().statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value()).extract().body().asString();

        var problemDetailResponse = objectMapper.readValue(responseJson, ProblemDetail.class);

        assertNotNull(problemDetailResponse);
        assertEquals("SimplePay Exception", problemDetailResponse.getTitle());
        assertEquals("CPF/CNPJ ou email já cadastrados", problemDetailResponse.getDetail());
        assertEquals(422, problemDetailResponse.getStatus());
    }

    private static WalletDTO buildWalletDTO(WalletType walletType) {
        return new WalletDTO(
                "Full Name Test Wallet",
                (walletType.equals(COMMON) ? "12312312344" : "34534534534534"),
                format("email_%s_teste@email.com", walletType.name()),
                "12345678",
                TEN,
                walletType);
    }
}
