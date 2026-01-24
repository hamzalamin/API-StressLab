package com.wora.apistresslab.repositories;

import com.wora.apistresslab.models.entities.LoadGenerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadGeneratorRepository extends JpaRepository<LoadGenerator, Long> {
}
