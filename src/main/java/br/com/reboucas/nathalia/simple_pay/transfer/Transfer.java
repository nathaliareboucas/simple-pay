package br.com.reboucas.nathalia.simple_pay.transfer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transfer")
public class Transfer {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "payer", nullable = false)
        private Long payer;

        @Column(name = "payee", nullable = false)
        private Long payee;

        @Column(name = "value", nullable = false)
        private BigDecimal value;

        @Column(name = "created_at", nullable = false)
        private LocalDateTime createdAt;
}
