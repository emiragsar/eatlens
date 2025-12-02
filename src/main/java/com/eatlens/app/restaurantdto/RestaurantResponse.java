package com.eatlens.app.restaurantdto;

import lombok.Data;

import java.time.LocalDateTime;

import com.eatlens.app.userdto.UserResponse;

@Data
public class RestaurantResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String district;
    private Double latitude;
    private Double longitude;
    private String phoneNumber;
    private String email;
    private String website;
    private String openingHours;
    private String cuisineType;
    private Integer priceRange;
    private Double averageRating;
    private Integer totalReviews;
    private Boolean isActive;
    private String logoUrl;
    private String coverImageUrl;
    private UserResponse owner;
    private LocalDateTime createdAt;
}