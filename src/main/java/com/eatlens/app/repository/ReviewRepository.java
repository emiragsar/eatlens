package com.eatlens.app.repository;

import com.eatlens.app.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByRestaurantId(Long restaurantId, Pageable pageable);

    Page<Review> findByCustomerId(Long customerId, Pageable pageable);

    boolean existsByCustomerIdAndRestaurantId(Long customerId, Long restaurantId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.restaurant.id = :restaurantId")
    Double calculateAverageRating(@Param("restaurantId") Long restaurantId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.restaurant.id = :restaurantId")
    Integer countReviewsByRestaurant(@Param("restaurantId") Long restaurantId);

    // En yardımcı yorumlar
    @Query("SELECT r FROM Review r WHERE r.restaurant.id = :restaurantId " +
            "ORDER BY r.helpfulCount DESC")
    Page<Review> findMostHelpfulReviews(@Param("restaurantId") Long restaurantId,
                                        Pageable pageable);

    // Analiz edilmemiş yorumlar
    List<Review> findByIsAnalyzedFalse();

    // Rating breakdown
    @Query("SELECT r.rating, COUNT(r) FROM Review r " +
            "WHERE r.restaurant.id = :restaurantId " +
            "GROUP BY r.rating")
    List<Object[]> getRatingBreakdown(@Param("restaurantId") Long restaurantId);
}

