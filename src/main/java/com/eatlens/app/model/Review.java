package com.eatlens.app.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "reviews")
@Data
@EqualsAndHashCode(callSuper = true)
public class Review extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    
    @Column(nullable = false)
    private Integer rating; // 1-5 arası
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @Column(name = "food_rating")
    private Integer foodRating;
    
    @Column(name = "service_rating")
    private Integer serviceRating;
    
    @Column(name = "ambiance_rating")
    private Integer ambianceRating;
    
    @Column(name = "price_rating")
    private Integer priceRating;
    
    @Column(name = "is_verified_purchase")
    private Boolean isVerifiedPurchase = false;
    
    @Column(name = "helpful_count")
    private Integer helpfulCount = 0;
    
    @Column(name = "image_urls", columnDefinition = "JSON")
    private String imageUrls; // JSON array of image URLs
    
    @Column(name = "owner_response", columnDefinition = "TEXT")
    private String ownerResponse;
    
    @Column(name = "is_analyzed")
    private Boolean isAnalyzed = false;
}