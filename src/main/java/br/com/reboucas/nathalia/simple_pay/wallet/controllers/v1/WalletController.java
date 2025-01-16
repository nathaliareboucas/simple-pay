package br.com.reboucas.nathalia.simple_pay.wallet.controllers.v1;

import br.com.reboucas.nathalia.simple_pay.wallet.controllers.v1.dtos.WalletDTO;
import br.com.reboucas.nathalia.simple_pay.wallet.services.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static br.com.reboucas.nathalia.simple_pay.wallet.controllers.v1.dtos.WalletDTO.build;

@RestController
@RequestMapping("/v1/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<WalletDTO> create(@RequestBody @Valid WalletDTO walletDTO) {
        var walletCreated = walletService.create(walletDTO.toWallet());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().
                path("/{id}").buildAndExpand(walletCreated.getId()).toUri();

        return ResponseEntity.created(uri).body(build(walletCreated));
    }
}
