package com.eatlens.app.aianalysisdto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class AIAnalysisResponse {
    private Long restaurantId;
    private String summary;
    private String sentiment;
    private Double sentimentScore;
    private List<String> positivePoints;
    private List<String> negativePoints;
    private String improvementSuggestions;
    private List<String> trendingTopics;
    private Map<String, Object> keywordAnalysis;
    private String recommendationText;
    private LocalDateTime lastAnalyzedAt;
    private Integer totalAnalyzedReviews;
}
