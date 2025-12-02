package com.eatlens.app.service.impl;


import com.eatlens.app.dto.reviewdto.ReviewCreateRequest;
import com.eatlens.app.exception.BusinessException;
import com.eatlens.app.exception.ResourceNotFoundException;
import com.eatlens.app.mapper.EntityMapper;
import com.eatlens.app.model.*;
import com.eatlens.app.repository.*;
import com.eatlens.app.service.ReviewService;
import com.eatlens.app.service.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.eatlens.app.dto.reviewdto.ReviewResponse;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final ReviewAnalysisQueueRepository queueRepository;
    private final RestaurantService restaurantService;
    private final EntityMapper mapper;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ReviewResponse createReview(ReviewCreateRequest request, Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restoran bulunamadı"));

        // Check if user already reviewed this restaurant
        if (reviewRepository.existsByCustomerIdAndRestaurantId(customerId, request.getRestaurantId())) {
            throw new BusinessException("Bu restoranı zaten değerlendirdiniz");
        }

        Review review = new Review();
        review.setCustomer(customer);
        review.setRestaurant(restaurant);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setFoodRating(request.getFoodRating());
        review.setServiceRating(request.getServiceRating());
        review.setAmbianceRating(request.getAmbianceRating());
        review.setPriceRating(request.getPriceRating());
        review.setIsVerifiedPurchase(false);
        review.setHelpfulCount(0);
        review.setIsAnalyzed(false);

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            try {
                review.setImageUrls(objectMapper.writeValueAsString(request.getImageUrls()));
            } catch (Exception e) {
                log.error("Error converting image URLs to JSON", e);
            }
        }

        Review savedReview = reviewRepository.save(review);

        // Add to AI analysis queue
        addToAnalysisQueue(savedReview);

        // Update restaurant rating
        restaurantService.updateRestaurantRating(restaurant.getId());

        log.info("Review created: {} for restaurant: {} by user: {}",
                savedReview.getId(), restaurant.getId(), customerId);

        return mapper.toReviewResponse(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getRestaurantReviews(Long restaurantId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewRepository.findByRestaurantId(restaurantId, pageable);

        return reviews.map(mapper::toReviewResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getUserReviews(Long userId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewRepository.findByCustomerId(userId, pageable);

        return reviews.map(mapper::toReviewResponse);
    }

    @Override
    public void addOwnerResponse(Long reviewId, String response, Long ownerId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Yorum bulunamadı"));

        // Check if owner owns the restaurant
        if (!review.getRestaurant().getOwner().getId().equals(ownerId)) {
            throw new BusinessException("Bu yoruma cevap verme yetkiniz yok");
        }

        review.setOwnerResponse(response);
        reviewRepository.save(review);

        log.info("Owner response added to review: {}", reviewId);
    }

    @Override
    public void markAsHelpful(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Yorum bulunamadı"));

        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Yorum bulunamadı"));

        if (!review.getCustomer().getId().equals(userId)) {
            throw new BusinessException("Bu yorumu silme yetkiniz yok");
        }

        review.setIsDeleted(true);
        reviewRepository.save(review);

        // Update restaurant rating
        restaurantService.updateRestaurantRating(review.getRestaurant().getId());

        log.info("Review deleted: {}", reviewId);
    }

    private void addToAnalysisQueue(Review review) {
        ReviewAnalysisQueue queue = new ReviewAnalysisQueue();
        queue.setReview(review);
        queue.setRestaurant(review.getRestaurant());
        queue.setStatus(QueueStatus.PENDING);
        queue.setRetryCount(0);

        queueRepository.save(queue);
        log.info("Review added to analysis queue: {}", review.getId());
    }
}