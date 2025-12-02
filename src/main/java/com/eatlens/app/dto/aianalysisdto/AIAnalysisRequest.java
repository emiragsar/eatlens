package com.eatlens.app.dto.aianalysisdto;

import lombok.Data;

@Data
public class AIAnalysisRequest {
    private Long restaurantId;
    private String analysisType; // FULL, SENTIMENT, SUMMARY
}
