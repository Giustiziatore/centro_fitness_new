package com.itscadmo.centro_fitness.Data.DAO;

import com.itscadmo.centro_fitness.Data.Entity.Istruttore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IstruttoreDAO extends JpaRepository<Istruttore, Long> {

    Optional<Istruttore> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
