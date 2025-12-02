package com.eatlens.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "review_analysis_queue")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReviewAnalysisQueue extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QueueStatus status; // PENDING, PROCESSING, COMPLETED, FAILED
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}

