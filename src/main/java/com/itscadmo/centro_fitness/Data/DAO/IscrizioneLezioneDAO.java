package com.itscadmo.centro_fitness.Data.DAO;

import com.itscadmo.centro_fitness.Data.Entity.IscrizioneLezione;
import com.itscadmo.centro_fitness.Data.Entity.StatoIscrizione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IscrizioneLezioneDAO extends JpaRepository<IscrizioneLezione, Long> {

    // Trova iscrizioni per lezione
    List<IscrizioneLezione> findByLezioneIdLezione(Long lezioneId);

    // Trova iscrizioni per cliente
    List<IscrizioneLezione> findByClienteId(Long clienteId);

    // Trova iscrizioni confermate per lezione
    List<IscrizioneLezione> findByLezioneIdLezioneAndStato(Long lezioneId, StatoIscrizione stato);

    // Verifica se cliente è già iscritto a una lezione
    boolean existsByLezioneIdLezioneAndClienteIdAndStato(
            Long lezioneId, Long clienteId, StatoIscrizione stato);

    // Trova iscrizione specifica
    Optional<IscrizioneLezione> findByLezioneIdLezioneAndClienteId(
            Long lezioneId, Long clienteId);

    // Conta iscritti confermati per lezione
    @Query("SELECT COUNT(i) FROM IscrizioneLezione i WHERE i.lezione.idLezione = :lezioneId AND i.stato = 'CONFERMATA'")
    long countIscrittiConfermati(@Param("lezioneId") Long lezioneId);
}