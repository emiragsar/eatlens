package com.eatlens.app.service;



import com.eatlens.app.dto.reviewdto.ReviewCreateRequest;
import com.eatlens.app.dto.reviewdto.ReviewResponse;
import org.springframework.data.domain.Page;

public interface ReviewService {
    ReviewResponse createReview(ReviewCreateRequest request, Long customerId);
    Page<ReviewResponse> getRestaurantReviews(Long restaurantId, int page, int size);
    Page<ReviewResponse> getUserReviews(Long userId, int page, int size);
    void addOwnerResponse(Long reviewId, String response, Long ownerId);
    void markAsHelpful(Long reviewId);
    void deleteReview(Long reviewId, Long userId);
}