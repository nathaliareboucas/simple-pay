package br.com.reboucas.nathalia.simple_pay.wallet;

import br.com.reboucas.nathalia.simple_pay.wallet.exceptions.InsufficientBalanceException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static br.com.reboucas.nathalia.simple_pay.wallet.WalletType.SHOPKEEPER;
import static java.lang.String.format;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallet")
public class Wallet {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "full_name", nullable = false)
        private String fullName;

        @Column(name = "cpf_cnpj", nullable = false, unique = true, length = 14)
        private String cpfCnpj;

        @Column(name = "email", nullable = false, unique = true)
        private String email;

        @Column(name = "password", nullable = false)
        private String password;

        @Column(name = "balance", nullable = false)
        private BigDecimal balance;

        @Column(name = "wallet_type", nullable = false)
        @Enumerated(EnumType.STRING)
        private WalletType walletType;

        public boolean isShopkeeper() {
                return this.walletType.equals(SHOPKEEPER);
        }

        public void credit(BigDecimal creditValue) {
                this.balance = this.balance.add(creditValue);
        }

        public void debit(BigDecimal debitValue) {
                if (this.balance.compareTo(debitValue) < 0)
                        throw new InsufficientBalanceException(format("Carteira %s com saldo insuficiente", this.id));

                this.balance = this.balance.subtract(debitValue);
        }
}
