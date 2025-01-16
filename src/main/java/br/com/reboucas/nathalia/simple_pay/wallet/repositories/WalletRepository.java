package br.com.reboucas.nathalia.simple_pay.wallet.repositories;

import br.com.reboucas.nathalia.simple_pay.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Boolean existsByCpfCnpjOrEmail(String cpfCnpj, String email);
}
