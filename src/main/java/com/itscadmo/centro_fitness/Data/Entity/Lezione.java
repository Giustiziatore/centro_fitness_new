package com.itscadmo.centro_fitness.Data.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lezioni")
@Data
public class Lezione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lezione")
    private Long idLezione;

    @Column(name = "data_ora_inizio", nullable = false)
    private LocalDateTime dataOraInizio;

    @Column(name = "data_ora_fine", nullable = false)
    private LocalDateTime dataOraFine;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato_lezione", nullable = false)
    private StatoLezione stato; //PROGRAMMATA, COMPLETATA, CANCELLATA

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "corso_id", nullable = false)
    private Corso corso;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "lezione_istruttore",
        joinColumns = @JoinColumn(name = "lezione_id"),
        inverseJoinColumns = @JoinColumn(name = "istruttore_id")
    )
    private List<Istruttore> istruttori = new ArrayList<>();

    @OneToMany(mappedBy = "lezione", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IscrizioneLezione> iscrizioniClienti = new ArrayList<>();

    @Transient
    public int getNumeroPartecipanti() {
        return iscrizioniClienti!= null? iscrizioniClienti.size() : 0;
    }

    @Transient
    public boolean isPiena() {
        return getNumeroPartecipanti() >= sala.getCapienza();
    }
}
