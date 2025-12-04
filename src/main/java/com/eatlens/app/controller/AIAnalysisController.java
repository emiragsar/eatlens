package com.eatlens.app.controller;


import com.eatlens.app.dto.aianalysisdto.AIAnalysisResponse;
import com.eatlens.app.dto.userdto.BaseResponse;
import com.eatlens.app.service.AIAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai-analysis")
@CrossOrigin(origins = "*")
public class AIAnalysisController {

    private final AIAnalysisService aiAnalysisService;

    public AIAnalysisController(AIAnalysisService aiAnalysisService) {
        this.aiAnalysisService = aiAnalysisService;
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<AIAnalysisResponse> getRestaurantAnalysis(
            @PathVariable Long restaurantId) {
        AIAnalysisResponse response = aiAnalysisService.getRestaurantAnalysis(restaurantId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/restaurants/{restaurantId}/trigger")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    public ResponseEntity<BaseResponse> triggerAnalysis(@PathVariable Long restaurantId) {
        aiAnalysisService.triggerAnalysis(restaurantId);
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Analiz başlatıldı");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<BaseResponse> receiveAnalysisResult(
            @RequestParam Long restaurantId,
            @RequestBody String analysisData) {
        aiAnalysisService.updateAnalysisSummary(restaurantId, analysisData);
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Analiz sonucu güncellendi");
        return ResponseEntity.ok(response);
    }
}