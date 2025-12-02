package com.eatlens.app.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_analysis_summary")
@Data
@EqualsAndHashCode(callSuper = true)
public class AIAnalysisSummary extends BaseEntity {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", unique = true, nullable = false)
    private Restaurant restaurant;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Enumerated(EnumType.STRING)
    private SentimentType sentiment; // POSITIVE, NEGATIVE, NEUTRAL, MIXED
    
    @Column(name = "sentiment_score")
    private Double sentimentScore; // 0-100
    
    @Column(name = "positive_points", columnDefinition = "JSON")
    private String positivePoints; // JSON array
    
    @Column(name = "negative_points", columnDefinition = "JSON")
    private String negativePoints; // JSON array
    
    @Column(name = "improvement_suggestions", columnDefinition = "TEXT")
    private String improvementSuggestions;
    
    @Column(name = "trending_topics", columnDefinition = "JSON")
    private String trendingTopics; // JSON array
    
    @Column(name = "keyword_analysis", columnDefinition = "JSON")
    private String keywordAnalysis; // JSON object
    
    @Column(name = "recommendation_text", columnDefinition = "TEXT")
    private String recommendationText;
    
    @Column(name = "last_analyzed_at")
    private LocalDateTime lastAnalyzedAt;
    
    @Column(name = "analysis_version")
    private String analysisVersion;
    
    @Column(name = "total_analyzed_reviews")
    private Integer totalAnalyzedReviews;
}

