package com.itscadmo.centro_fitness.Data.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Table(name = "sala")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sala")
    private Long idSala;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "capienza")
    private int capienza;

    @Column(name = "orari_apertura")
    private String orariApertura;
    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Corso> corsi = new ArrayList<>();

}
