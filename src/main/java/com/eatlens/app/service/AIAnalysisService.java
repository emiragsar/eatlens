package com.eatlens.app.service;


import com.eatlens.app.dto.aianalysisdto.AIAnalysisResponse;

public interface AIAnalysisService {
    AIAnalysisResponse getRestaurantAnalysis(Long restaurantId);
    void triggerAnalysis(Long restaurantId);
    void processAnalysisQueue();
    void updateAnalysisSummary(Long restaurantId, String analysisData);
}