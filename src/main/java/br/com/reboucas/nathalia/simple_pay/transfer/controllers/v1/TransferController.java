package br.com.reboucas.nathalia.simple_pay.transfer.controllers.v1;

import br.com.reboucas.nathalia.simple_pay.transfer.controllers.v1.dtos.TransferDTO;
import br.com.reboucas.nathalia.simple_pay.transfer.services.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static br.com.reboucas.nathalia.simple_pay.transfer.controllers.v1.dtos.TransferDTO.build;

@RestController
@RequestMapping("/v1/api/transfer")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<TransferDTO> create(@RequestBody @Valid TransferDTO transferDTO) {
        var tranfer = transferService.create(transferDTO.toTransfer());

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(tranfer.getId()).toUri();

        return ResponseEntity.created(uri).body(build(tranfer));
    }
}
