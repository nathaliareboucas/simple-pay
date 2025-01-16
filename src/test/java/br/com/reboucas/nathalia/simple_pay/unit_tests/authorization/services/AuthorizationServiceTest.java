package br.com.reboucas.nathalia.simple_pay.unit_tests.authorization.services;

import br.com.reboucas.nathalia.simple_pay.authorization.Authorization;
import br.com.reboucas.nathalia.simple_pay.authorization.AuthorizationData;
import br.com.reboucas.nathalia.simple_pay.authorization.exceptions.UnauthorizedException;
import br.com.reboucas.nathalia.simple_pay.authorization.services.AuthorizationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withForbiddenRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(AuthorizationService.class)
class AuthorizationServiceTest {
    private static final String AUTHORIZATION_CLIENT_URL = "https://util.devi.tools/api/v2/authorize";

    @Autowired
    AuthorizationService authorizationService;

    @Autowired
    MockRestServiceServer server;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldAuthorize() throws JsonProcessingException {
        var expectedAuthorization = getExpectedAuthorization();
        var expectedAuthorizationJson = objectMapper.writeValueAsString(expectedAuthorization);
        server.expect(requestTo(AUTHORIZATION_CLIENT_URL))
                .andRespond(withSuccess(expectedAuthorizationJson, MediaType.APPLICATION_JSON));

        authorizationService.authorize(1L, 2L, TEN);

        server.verify();
        assertNotNull(expectedAuthorization);
        assertNotNull(expectedAuthorization.getData());
    }

    @Test
    void shouldNotAuthorize() {
        server.expect(requestTo(AUTHORIZATION_CLIENT_URL))
                .andRespond(withForbiddenRequest());

        var exception = assertThrowsExactly(UnauthorizedException.class,
                () -> authorizationService.authorize(1L, 2L, TEN));

        server.verify();
        assertInstanceOf(UnauthorizedException.class, exception);
        assertEquals("Transação não autorizada", exception.getMessage());
    }

    private static Authorization getExpectedAuthorization() {
        var authorizationData = new AuthorizationData();
        authorizationData.setAuthorization(true);

        var authorization = new Authorization();
        authorization.setStatus("success");
        authorization.setData(authorizationData);

        return authorization;
    }

}