package com.itscadmo.centro_fitness.Data.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "iscrizione_lezione")
@Data
public class IscrizioneLezione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_iscrizione")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lezione_id", nullable = false)
    private Lezione lezione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "data_iscrizione", nullable = false)
    private LocalDateTime dataIscrizione;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato_iscrizione")
    private StatoIscrizione stato; // CONFERMATA, CANCELLATA

    @PrePersist
    protected void onCreate() {
        if (dataIscrizione == null) {
            dataIscrizione = LocalDateTime.now();
        }
        if (stato == null) {
            stato = StatoIscrizione.CONFERMATA;
        }
    }
}