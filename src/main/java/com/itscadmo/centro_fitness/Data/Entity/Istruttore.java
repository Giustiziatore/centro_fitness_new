package com.itscadmo.centro_fitness.Data.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "istruttore")
@PrimaryKeyJoinColumn(name = "id")
public class Istruttore extends Utente {


    @Column(name = "specializzazione")
    private String specializzazione;

    @Column(name = "orari_disponibilita")
    private String orariDisponibilita;

    @OneToMany(mappedBy = "istruttoreCorso")
    private List<Corso> corsiInsegnati = new ArrayList<>();



}
