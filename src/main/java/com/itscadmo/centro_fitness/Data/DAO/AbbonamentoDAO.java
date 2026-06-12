package com.itscadmo.centro_fitness.Data.DAO;

import com.itscadmo.centro_fitness.Data.Entity.Abbonamento;
import com.itscadmo.centro_fitness.Data.Entity.StatoAbbonamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbbonamentoDAO extends JpaRepository<Abbonamento, Long> {
    
    List<Abbonamento> findByClienteId(Long clienteId);
    
    List<Abbonamento> findByDataFineBeforeAndStato(LocalDate data, StatoAbbonamento stato);
    
    // Query per verificare abbonamento attivo
    @Query("SELECT COUNT(a) > 0 FROM Abbonamento a " +
           "WHERE a.cliente.id = :clienteId " +
           "AND a.stato = 'ATTIVO' " +
           "AND a.dataFine >= :oggi")
    boolean existsAbbonamentiAttiviByClienteId(
        @Param("clienteId") Long clienteId, 
        @Param("oggi") LocalDate oggi
    );
}