package com.eatlens.app.repository;

import com.eatlens.app.model.AIAnalysisSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface AIAnalysisSummaryRepository extends JpaRepository<AIAnalysisSummary, Long> {

    Optional<AIAnalysisSummary> findByRestaurantId(Long restaurantId);

    @Query("SELECT a FROM AIAnalysisSummary a WHERE a.lastAnalyzedAt < :date")
    List<AIAnalysisSummary> findOutdatedAnalyses(LocalDateTime date);
}
