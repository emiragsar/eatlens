package com.eatlens.app.service.impl;


import com.eatlens.app.dto.aianalysisdto.AIAnalysisResponse;
import com.eatlens.app.exception.ResourceNotFoundException;
import com.eatlens.app.model.*;
import com.eatlens.app.repository.AIAnalysisSummaryRepository;
import com.eatlens.app.repository.RestaurantRepository;
import com.eatlens.app.repository.ReviewAnalysisQueueRepository;
import com.eatlens.app.repository.ReviewRepository;
import com.eatlens.app.service.AIAnalysisService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class AIAnalysisServiceImpl implements AIAnalysisService {

    private final AIAnalysisSummaryRepository analysisSummaryRepository;
    private final ReviewAnalysisQueueRepository queueRepository;
    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AIAnalysisServiceImpl(AIAnalysisSummaryRepository analysisSummaryRepository, ReviewAnalysisQueueRepository queueRepository, ReviewRepository reviewRepository,
                                 RestaurantRepository restaurantRepository, RestTemplate restTemplate, ObjectMapper objectMapper)
        {
        this.analysisSummaryRepository = analysisSummaryRepository;
        this.queueRepository = queueRepository;
        this.reviewRepository = reviewRepository;
        this.restaurantRepository = restaurantRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        }


    @Value("${ai.service.url:http://localhost:5000}")
    private String aiServiceUrl;

    @Override
    @Transactional(readOnly = true)
    public AIAnalysisResponse getRestaurantAnalysis(Long restaurantId) {
        AIAnalysisSummary summary = analysisSummaryRepository.findByRestaurantId(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Analiz bulunamadı"));

        return convertToResponse(summary);
    }

    @Override
    @Async
    public void triggerAnalysis(Long restaurantId) {
        log.info("Triggering AI analysis for restaurant: {}", restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restoran bulunamadı"));

        List<Review> reviews = reviewRepository.findByRestaurantId(restaurantId, null).getContent();

        if (reviews.isEmpty()) {
            log.warn("No reviews found for restaurant: {}", restaurantId);
            return;
        }

        try {
            // Prepare request for Python AI service
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("restaurant_id", restaurantId);
            requestBody.put("reviews", reviews.stream().map(review -> {
                Map<String, Object> reviewMap = new HashMap<>();
                reviewMap.put("id", review.getId());
                reviewMap.put("rating", review.getRating());
                reviewMap.put("comment", review.getComment());
                reviewMap.put("food_rating", review.getFoodRating());
                reviewMap.put("service_rating", review.getServiceRating());
                reviewMap.put("ambiance_rating", review.getAmbianceRating());
                reviewMap.put("price_rating", review.getPriceRating());
                return reviewMap;
            }).collect(Collectors.toList()));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Call Python AI service
            Map<String, Object> response = restTemplate.postForObject(
                    aiServiceUrl + "/analyze",
                    entity,
                    Map.class
            );

            // Save analysis results
            saveAnalysisResults(restaurantId, response);

            // Mark reviews as analyzed
            reviews.forEach(review -> {
                review.setIsAnalyzed(true);
                reviewRepository.save(review);
            });

            log.info("AI analysis completed for restaurant: {}", restaurantId);

        } catch (Exception e) {
            log.error("Error during AI analysis for restaurant: {}", restaurantId, e);
            throw new RuntimeException("AI analizi sırasında hata oluştu", e);
        }
    }

    @Override
    @Scheduled(fixedDelay = 60000) // Her 1 dakikada bir
    public void processAnalysisQueue() {
        List<ReviewAnalysisQueue> pendingQueues = queueRepository.findPendingQueues();

        if (pendingQueues.isEmpty()) {
            return;
        }

        log.info("Processing {} pending analysis queues", pendingQueues.size());

        // Group by restaurant for batch processing
        // Group by restaurant for batch processing
        Map<Long, List<ReviewAnalysisQueue>> groupedByRestaurant = pendingQueues.stream()
                .collect(Collectors.groupingBy(q -> q.getRestaurant().getId()));

        for (Map.Entry<Long, List<ReviewAnalysisQueue>> entry : groupedByRestaurant.entrySet()) {
            Long restaurantId = entry.getKey();
            List<ReviewAnalysisQueue> queues = entry.getValue();

            // If there are more than 5 reviews waiting, trigger analysis
            if (queues.size() >= 5) {
                try {
                    // Update status to PROCESSING
                    queues.forEach(q -> {
                        q.setStatus(QueueStatus.PROCESSING);
                        queueRepository.save(q);
                    });

                    triggerAnalysis(restaurantId);

                    // Update status to COMPLETED
                    queues.forEach(q -> {
                        q.setStatus(QueueStatus.COMPLETED);
                        q.setProcessedAt(LocalDateTime.now());
                        queueRepository.save(q);
                    });

                } catch (Exception e) {
                    log.error("Error processing queue for restaurant: {}", restaurantId, e);

                    // Update status to FAILED
                    queues.forEach(q -> {
                        q.setStatus(QueueStatus.FAILED);
                        q.setRetryCount(q.getRetryCount() + 1);
                        q.setErrorMessage(e.getMessage());
                        queueRepository.save(q);
                    });
                }
            }
        }
    }

    @Override
    public void updateAnalysisSummary(Long restaurantId, String analysisData) {
        try {
            Map<String, Object> data = objectMapper.readValue(analysisData, new TypeReference<>() {});
            saveAnalysisResults(restaurantId, data);
        } catch (Exception e) {
            log.error("Error updating analysis summary for restaurant: {}", restaurantId, e);
            throw new RuntimeException("Analiz özeti güncellenirken hata oluştu", e);
        }
    }

    private void saveAnalysisResults(Long restaurantId, Map<String, Object> analysisData) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restoran bulunamadı"));

        AIAnalysisSummary summary = analysisSummaryRepository.findByRestaurantId(restaurantId)
                .orElse(new AIAnalysisSummary());

        summary.setRestaurant(restaurant);
        summary.setSummary((String) analysisData.get("summary"));
        summary.setSentiment(SentimentType.valueOf((String) analysisData.get("sentiment")));
        summary.setSentimentScore((Double) analysisData.get("sentiment_score"));

        try {
            summary.setPositivePoints(objectMapper.writeValueAsString(analysisData.get("positive_points")));
            summary.setNegativePoints(objectMapper.writeValueAsString(analysisData.get("negative_points")));
            summary.setTrendingTopics(objectMapper.writeValueAsString(analysisData.get("trending_topics")));
            summary.setKeywordAnalysis(objectMapper.writeValueAsString(analysisData.get("keyword_analysis")));
        } catch (Exception e) {
            log.error("Error converting analysis data to JSON", e);
        }

        summary.setImprovementSuggestions((String) analysisData.get("improvement_suggestions"));
        summary.setRecommendationText((String) analysisData.get("recommendation"));
        summary.setLastAnalyzedAt(LocalDateTime.now());
        summary.setAnalysisVersion((String) analysisData.getOrDefault("version", "1.0"));
        summary.setTotalAnalyzedReviews((Integer) analysisData.getOrDefault("total_reviews", 0));

        analysisSummaryRepository.save(summary);
        log.info("Analysis summary saved for restaurant: {}", restaurantId);
    }

    private AIAnalysisResponse convertToResponse(AIAnalysisSummary summary) {
        AIAnalysisResponse response = new AIAnalysisResponse();
        response.setRestaurantId(summary.getRestaurant().getId());
        response.setSummary(summary.getSummary());
        response.setSentiment(summary.getSentiment().toString());
        response.setSentimentScore(summary.getSentimentScore());
        response.setImprovementSuggestions(summary.getImprovementSuggestions());
        response.setRecommendationText(summary.getRecommendationText());
        response.setLastAnalyzedAt(summary.getLastAnalyzedAt());
        response.setTotalAnalyzedReviews(summary.getTotalAnalyzedReviews());

        try {
            if (summary.getPositivePoints() != null) {
                response.setPositivePoints(objectMapper.readValue(summary.getPositivePoints(), new TypeReference<>() {}));
            }
            if (summary.getNegativePoints() != null) {
                response.setNegativePoints(objectMapper.readValue(summary.getNegativePoints(), new TypeReference<>() {}));
            }
            if (summary.getTrendingTopics() != null) {
                response.setTrendingTopics(objectMapper.readValue(summary.getTrendingTopics(), new TypeReference<>() {}));
            }
            if (summary.getKeywordAnalysis() != null) {
                response.setKeywordAnalysis(objectMapper.readValue(summary.getKeywordAnalysis(), new TypeReference<>() {}));
            }
        } catch (Exception e) {
            log.error("Error parsing JSON data", e);
        }

        return response;
    }
}