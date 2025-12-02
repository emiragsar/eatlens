package com.eatlens.app.controller;


import com.eatlens.app.dto.reviewdto.ReviewCreateRequest;
import com.eatlens.app.dto.reviewdto.ReviewResponse;
import com.eatlens.app.dto.userdto.BaseResponse;
import com.eatlens.app.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ReviewResponse> createReview(
            @RequestHeader("userId") Long userId,
            @Valid @RequestBody ReviewCreateRequest request) {
        ReviewResponse response = reviewService.createReview(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<Page<ReviewResponse>> getRestaurantReviews(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ReviewResponse> response = reviewService.getRestaurantReviews(restaurantId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ReviewResponse>> getMyReviews(
            @RequestHeader("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ReviewResponse> response = reviewService.getUserReviews(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reviewId}/owner-response")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<BaseResponse> addOwnerResponse(
            @PathVariable Long reviewId,
            @RequestHeader("userId") Long userId,
            @RequestParam String response) {
        reviewService.addOwnerResponse(reviewId, response, userId);
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setSuccess(true);
        baseResponse.setMessage("Yanıt başarıyla eklendi");
        return ResponseEntity.ok(baseResponse);
    }

    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<BaseResponse> markAsHelpful(@PathVariable Long reviewId) {
        reviewService.markAsHelpful(reviewId);
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Yorum faydalı olarak işaretlendi");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader("userId") Long userId) {
        reviewService.deleteReview(reviewId, userId);
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Yorum başarıyla silindi");
        return ResponseEntity.ok(response);
    }
}