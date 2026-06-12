package com.itscadmo.centro_fitness.Data.DAO;

import com.itscadmo.centro_fitness.Data.Entity.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaDAO extends JpaRepository<Sala, Long> {
    Optional<Sala> findByNome(String nome);

    boolean existsByNome(String nome);
}
