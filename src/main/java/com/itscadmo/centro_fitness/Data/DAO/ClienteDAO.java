package com.itscadmo.centro_fitness.Data.DAO;

import com.itscadmo.centro_fitness.Data.Entity.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteDAO extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
