package com.itscadmo.centro_fitness.Data.DAO;

import com.itscadmo.centro_fitness.Data.Entity.Lezione;
import com.itscadmo.centro_fitness.Data.Entity.StatoLezione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LezioneDAO extends JpaRepository<Lezione, Long> {

    // Trova lezioni per corso
    List<Lezione> findByCorsoIdCorso(Long corsoId);

    // Trova lezioni per sala
    List<Lezione> findBySalaIdSala(Long salaId);

    // Trova lezioni per istruttore
    @Query("SELECT l FROM Lezione l JOIN l.istruttori i WHERE i.id = :istruttoreId")
    List<Lezione> findByIstruttoreId(@Param("istruttoreId") Long istruttoreId);

    // Trova lezioni future
    List<Lezione> findByDataOraInizioAfterAndStato(LocalDateTime data, StatoLezione stato);

    // Trova lezioni in un range di date
    List<Lezione> findByDataOraInizioBetween(LocalDateTime start, LocalDateTime end);

    // Trova lezioni per corso e stato
    List<Lezione> findByCorsoIdCorsoAndStato(Long corsoId, StatoLezione stato);

    // Verifica conflitti sala (lezioni sovrapposte nella stessa sala)
    @Query("SELECT l FROM Lezione l WHERE l.sala.idSala = :salaId " +
            "AND l.stato != 'CANCELLATA' " +
            "AND ((l.dataOraInizio < :fine AND l.dataOraFine > :inizio))")
    List<Lezione> findConflittiSala(
            @Param("salaId") Long salaId,
            @Param("inizio") LocalDateTime inizio,
            @Param("fine") LocalDateTime fine
    );

    // Verifica conflitti istruttore (lezioni sovrapposte per stesso istruttore)
    @Query("SELECT l FROM Lezione l JOIN l.istruttori i WHERE i.id = :istruttoreId " +
            "AND l.stato != 'CANCELLATA' " +
            "AND ((l.dataOraInizio < :fine AND l.dataOraFine > :inizio))")
    List<Lezione> findConflittiIstruttore(
            @Param("istruttoreId") Long istruttoreId,
            @Param("inizio") LocalDateTime inizio,
            @Param("fine") LocalDateTime fine
    );
}