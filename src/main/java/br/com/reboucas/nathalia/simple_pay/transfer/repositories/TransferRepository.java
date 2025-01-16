package br.com.reboucas.nathalia.simple_pay.transfer.repositories;

import br.com.reboucas.nathalia.simple_pay.transfer.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
