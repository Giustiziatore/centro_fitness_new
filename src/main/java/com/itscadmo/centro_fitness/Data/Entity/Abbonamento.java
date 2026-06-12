package com.itscadmo.centro_fitness.Data.Entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "abbonamento")
public class Abbonamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAbbonamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato_abbonamento", nullable = false)
    private StatoAbbonamento stato;

    @Enumerated(EnumType.STRING)
    @Column(name = "durata", nullable = false)
    private DurataAbbonamento durata;

    @Column(name = "prezzo", nullable = false)
    private double prezzo;

    @Column(name = "data_inizio", nullable = false)
    private LocalDate dataInizio;

    @Column(name = "data_fine", nullable = false)
    private LocalDate dataFine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

}