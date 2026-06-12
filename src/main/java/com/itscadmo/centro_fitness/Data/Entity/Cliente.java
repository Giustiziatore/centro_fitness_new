package com.itscadmo.centro_fitness.Data.Entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "id")
public class Cliente extends Utente {

    @Column(name = "obiettivo_fitness")
    private String obiettivoFitness;

    @Column(name = "numero_tessera", unique = true, nullable = false)
    private String numeroTessera;

    @Column(name = "data_tesseramento")
    private LocalDate dataTesseramento;

    @Column(name = "data_scadenza_tessera")
    private LocalDate dataScadenzaTessera;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Abbonamento> abbonamenti = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "istruttore_id")
    private Istruttore istruttore;
}