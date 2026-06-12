package com.itscadmo.centro_fitness.Data.Entity;


import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "corso")
public class Corso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_corso")
    private Long idCorso;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "giorno_settimana")
    private String giornoSettimana;

    @Column(name = "descrizione")
    private String descrizione;

    @Column(name = "durata_minuti")
    private Integer durataMinuti;

    @ManyToOne
    @JoinColumn(name = "id_sala", nullable = false)
    private Sala sala;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_istruttore", nullable = false)
    private Istruttore istruttoreCorso;

    @OneToMany(mappedBy = "corso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Lezione> lezioni;

}
