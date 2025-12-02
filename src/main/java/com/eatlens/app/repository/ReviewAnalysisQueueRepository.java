package com.eatlens.app.repository;

import com.eatlens.app.model.QueueStatus;
import com.eatlens.app.model.ReviewAnalysisQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface ReviewAnalysisQueueRepository extends JpaRepository<ReviewAnalysisQueue, Long> {

    List<ReviewAnalysisQueue> findByStatus(QueueStatus status);

    @Query("SELECT q FROM ReviewAnalysisQueue q WHERE q.status = 'PENDING' " +
            "ORDER BY q.createdAt ASC")
    List<ReviewAnalysisQueue> findPendingQueues();

    @Query("SELECT COUNT(q) FROM ReviewAnalysisQueue q WHERE " +
            "q.restaurant.id = :restaurantId AND q.status = 'PENDING'")
    Long countPendingByRestaurant(Long restaurantId);
}
