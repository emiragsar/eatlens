package com.eatlens.app.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "restaurants")
@Data
@EqualsAndHashCode(callSuper = true)
public class Restaurant extends BaseEntity {
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String address;
    
    private String city;
    
    private String district;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    private Double latitude;
    
    private Double longitude;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    private String email;
    
    private String website;
    
    @Column(name = "opening_hours", columnDefinition = "JSON")
    private String openingHours; // JSON format
    
    @Column(name = "cuisine_type")
    private String cuisineType;
    
    @Column(name = "price_range")
    private Integer priceRange; // 1-5 arası
    
    @Column(name = "average_rating")
    private Double averageRating = 0.0;
    
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "logo_url")
    private String logoUrl;
    
    @Column(name = "cover_image_url")
    private String coverImageUrl;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Category> categories;
    
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<MenuItem> menuItems;
    
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Review> reviews;
    
    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private AIAnalysisSummary aiAnalysis;
}