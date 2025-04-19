package com.RitabrataProject.spring_backend.repository;

import com.RitabrataProject.spring_backend.model.SummarizationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummarizationHistoryRepository extends JpaRepository<SummarizationHistory, Long> {
    // You can define custom query methods here, if needed.
}
