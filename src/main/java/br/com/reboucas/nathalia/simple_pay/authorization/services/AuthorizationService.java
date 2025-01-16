package br.com.reboucas.nathalia.simple_pay.authorization.services;

import br.com.reboucas.nathalia.simple_pay.authorization.Authorization;
import br.com.reboucas.nathalia.simple_pay.authorization.exceptions.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@Slf4j
public class AuthorizationService {
    private final RestClient restClient;

    public AuthorizationService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://util.devi.tools/api/v2/authorize").build();
    }

    public void authorize(Long payer, Long payee, BigDecimal value) {
        log.info("Autorizando transferência de R${} do pagador {} para o beneficiário {}",
                value, payer, payee);

        var authorization = restClient.get()
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new UnauthorizedException("Transação não autorizada");
                }).body(Authorization.class);

        if (!isEmpty(authorization) && !isEmpty(authorization.getData()))
            log.info("Transação autorizada - autorização: {}", authorization.getData().isAuthorization());
    }
}
