package com.itscadmo.centro_fitness.Data.DAO;

import com.itscadmo.centro_fitness.Data.Entity.Corso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorsoDAO extends JpaRepository<Corso, Long> {

    // Trova corsi per sala
    List<Corso> findBySalaIdSala(Long salaId);

    // Trova corsi per istruttore
    List<Corso> findByIstruttoreCorsoId(Long istruttoreId);

    // Trova corsi per giorno settimana
    List<Corso> findByGiornoSettimana(String giornoSettimana);

    // Trova corsi per nome (case insensitive)
    @Query("SELECT c FROM Corso c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Corso> findByNomeContaining(@Param("nome") String nome);

    // Verifica se esiste già un corso con stesso nome, sala e giorno
    boolean existsByNomeAndSalaIdSalaAndGiornoSettimana(
            String nome, Long salaId, String giornoSettimana);
}